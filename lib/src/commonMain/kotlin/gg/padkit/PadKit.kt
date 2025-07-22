/*
 * Copyright (c) Jam.gg 2024.
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import gg.padkit.config.HapticFeedbackType
import gg.padkit.handlers.Pointer
import gg.padkit.haptics.InputHapticGenerator
import gg.padkit.haptics.rememberHapticGenerator
import gg.padkit.ids.ControlId
import gg.padkit.inputevents.InputEvent
import gg.padkit.inputevents.InputEventsGenerator
import gg.padkit.inputstate.InputState

@Composable
fun PadKit(
    modifier: Modifier = Modifier,
    onInputStateUpdated: ((InputState) -> Unit)? = null,
    onInputEvents: ((List<InputEvent>) -> Unit)? = null,
    hapticFeedbackType: HapticFeedbackType = HapticFeedbackType.PRESS,
    simulatedControlIds: State<Set<ControlId>> = mutableStateOf(emptySet()),
    simulatedState: State<InputState> = mutableStateOf(InputState()),
    content: @Composable PadKitScope.() -> Unit,
) {
    val scope = remember { PadKitScope() }
    val rootPosition = remember { mutableStateOf(Offset.Zero) }

    val inputEventsGenerator = remember { InputEventsGenerator() }
    val hapticGenerator = rememberHapticGenerator()
    val inputHapticGenerator =
        remember(hapticFeedbackType) {
            InputHapticGenerator(hapticGenerator, hapticFeedbackType, scope.inputState.value)
        }

    LaunchedEffect(simulatedState.value) {
        scope.inputState.value =
            scope.handleSimulatedInputEvents(
                simulatedControlIds.value,
                scope.inputState.value,
                simulatedState.value,
            )
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onGloballyPositioned { rootPosition.value = it.positionInRoot() }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val pointers =
                                event.changes
                                    .asSequence()
                                    .filter { it.pressed }
                                    .map { Pointer(it.id.value, it.position + rootPosition.value) }

                            val updatedInputState = scope.handleInputEvent(pointers)
                            scope.inputState.value =
                                scope.handleSimulatedInputEvents(
                                    simulatedControlIds.value,
                                    updatedInputState,
                                    simulatedState.value,
                                )
                        }
                    }
                },
    ) {
        scope.content()
    }

    LaunchedEffect(scope.inputState) {
        snapshotFlow { scope.inputState.value }
            .collect { inputState ->
                onInputStateUpdated?.invoke(inputState)
                onInputEvents?.invoke(inputEventsGenerator.onInputStateChanged(inputState))
                inputHapticGenerator.onInputStateChanged(inputState)
            }
    }
}
