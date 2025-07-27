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

package gg.padkit.handlers

import androidx.compose.ui.geometry.Offset
import gg.padkit.anchors.Anchor
import gg.padkit.ids.Id
import gg.padkit.inputstate.InputState
import gg.padkit.inputstate.setDigitalKeyIfPressed
import gg.padkit.utils.coerceIn
import kotlinx.collections.immutable.PersistentSet

internal class FaceButtonsPointerHandler(
    private val anchors: List<Anchor<Id.Key>>,
    private val trackPointers: Boolean,
) : PointerHandler {
    private val keys =
        anchors
            .flatMap { it.buttons }
            .toSet()

    override fun handle(
        pointers: List<Pointer>,
        inputState: InputState,
        trackedIds: Set<Long>,
        data: Any?,
    ): Result {
        val pressedKeys =
            pointers
                .flatMap { pointer -> findClosestAnchor(pointer) }
                .toSet()

        val finalState =
            keys.fold(inputState) { updatedState, key ->
                updatedState.setDigitalKeyIfPressed(key, key in pressedKeys)
            }

        val updatedTrackedIndices =
            if (trackPointers) {
                pointers
                    .map { it.pointerId }
                    .toSet()
            } else {
                emptySet()
            }

        return Result(finalState, updatedTrackedIndices)
    }

    private fun findClosestAnchor(pointer: Pointer): PersistentSet<Id.Key> {
        return anchors
            .minBy {
                val coercedPosition = pointer.position.coerceIn(Offset(-1f, -1f), Offset(1f, 1f))
                it.distance(coercedPosition)
            }
            .buttons
    }
}
