/*
 * Copyright (c) Jam.gg 2024.
 * Copyright (c) Filippo Scognamiglio 2025.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gg.padkit

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import gg.padkit.handlers.Pointer
import gg.padkit.handlers.PointerHandler
import gg.padkit.ids.Id
import gg.padkit.inputstate.InputState
import gg.padkit.utils.relativeToCenter

/**
 * A scope for the children of the [PadKit] composable.
 */
@Stable
class PadKitScope {
    private data class HandlerState(
        val pointerHandler: PointerHandler,
        var trackedIds: Set<Long>,
        var rect: Rect = Rect.Zero,
        val data: Any? = null,
    )

    internal val inputState = mutableStateOf(InputState())

    private val handlers = mutableMapOf<PointerHandler, HandlerState>()

    internal fun registerHandler(
        pointerHandler: PointerHandler,
        data: Any? = null,
    ) {
        handlers[pointerHandler] = HandlerState(pointerHandler, emptySet(), Rect.Zero, data)
    }

    internal fun unregisterHandler(pointerHandler: PointerHandler) {
        handlers.remove(pointerHandler)
    }

    internal fun updateHandlerPosition(
        pointerHandler: PointerHandler,
        rect: Rect,
    ) {
        handlers[pointerHandler]?.rect = rect
    }

    private fun getAllHandlers(): Collection<HandlerState> {
        return handlers.values
    }

    private fun getTrackedIds(): Set<Long> {
        return handlers.values
            .flatMap { it.trackedIds }
            .toSet()
    }

    private fun getHandlerAtPosition(position: Offset): PointerHandler? {
        return handlers.values
            .firstOrNull { it.rect.contains(position) }
            ?.pointerHandler
    }

    private fun getHandlerTracking(pointerId: Long): PointerHandler? {
        return handlers.values
            .firstOrNull { it.trackedIds.contains(pointerId) }
            ?.pointerHandler
    }

    internal fun handleInputEvent(eventPointers: Sequence<Pointer>): InputState {
        val trackedPointers = getTrackedIds()

        val handlersAssociations: Map<PointerHandler?, List<Pointer>> =
            eventPointers
                .groupBy { pointer ->
                    if (pointer.pointerId in trackedPointers) {
                        getHandlerTracking(pointer.pointerId)
                    } else {
                        getHandlerAtPosition(pointer.position)
                    }
                }

        return getAllHandlers()
            .fold(InputState()) { state, handler ->
                val pointerHandler = handler.pointerHandler
                val pointers =
                    handlersAssociations.getOrElse(pointerHandler) { emptyList() }

                val relativePointers =
                    pointers
                        .map {
                            Pointer(
                                it.pointerId,
                                it.position.relativeToCenter(handler.rect),
                            )
                        }
                        .filter { pointer ->
                            val isClose = pointer.position.getDistanceSquared() <= 1f
                            val isTracked = handler.trackedIds.contains(pointer.pointerId)
                            isClose || isTracked
                        }

                val (updatedState, updatedTrackedIndices) =
                    pointerHandler.handle(
                        relativePointers,
                        state,
                        handler.trackedIds,
                        handler.data,
                    )

                handler.trackedIds = updatedTrackedIndices
                updatedState
            }
    }

    internal fun handleSimulatedInputEvents(
        simulatedControlIds: Set<Id>,
        inputState: InputState,
        simulatedInputState: InputState,
    ): InputState {
        return simulatedControlIds.fold(inputState) { state, id ->
            when (id) {
                is Id.Key -> state.setDigitalKey(id, simulatedInputState.getDigitalKey(id))
                is Id.ContinuousDirection -> state.setContinuousDirection(id, simulatedInputState.getContinuousDirection(id))
                is Id.DiscreteDirection -> state.setDiscreteDirection(id, simulatedInputState.getDiscreteDirection(id))
            }
        }
    }
}
