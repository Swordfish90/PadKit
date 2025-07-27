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
import gg.padkit.ids.Id
import gg.padkit.inputevents.InputEvent
import gg.padkit.inputevents.InputEventsGenerator
import gg.padkit.inputstate.InputState

/**
 * A layout composable that creates a virtual gamepad.
 *
 * PadKit places its children inside a [PadKitScope], allowing you to use various controls like
 * [gg.padkit.controls.ControlAnalog], [gg.padkit.controls.ControlButton], [gg.padkit.controls.ControlCross], and [gg.padkit.controls.ControlFaceButtons]. It listens for
 * pointer (touch) events within its bounds and updates the state of the controls accordingly.
 *
 * The most recent state of all controls is available through the [onInputStateUpdated] callback,
 * which provides an [InputState] object. For a stream of discrete input changes, use the
 * [onInputEvents] callback.
 *
 * By default, PadKit fills the maximum size available to it. You can change this behavior by
 * providing a different `modifier`. The aspect ratio of the gamepad can be controlled via the
 * modifier chain, which is useful for maintaining a consistent layout across different screen sizes.
 *
 * For testing or programmatic control, you can use [simulatedState] and [simulatedControlIds]
 * to override the input from user touches for specific controls.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param onInputStateUpdated A callback that is invoked frequently with the complete, up-to-date
 * [InputState] of all controls on the gamepad.
 * @param onInputEvents A callback that is invoked only when a control's state changes, providing a
 * list of specific [InputEvent]s that have occurred.
 * @param hapticFeedbackType The type of haptic feedback to generate when a control is used.
 * @param simulatedControlIds A set of [Id]s that should be controlled by the [simulatedState]
 * instead of user input. This is useful for testing or creating demos.
 * @param simulatedState The [InputState] to use for the controls specified in [simulatedControlIds].
 * @param content A lambda that receives a [PadKitScope] where you can define the layout of the
 * gamepad by adding control composables.
 */
@Composable
fun PadKit(
    modifier: Modifier = Modifier,
    onInputStateUpdated: ((InputState) -> Unit)? = null,
    onInputEvents: ((List<InputEvent>) -> Unit)? = null,
    hapticFeedbackType: HapticFeedbackType = HapticFeedbackType.PRESS,
    simulatedControlIds: State<Set<Id>> = mutableStateOf(emptySet()),
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

    LaunchedEffect(scope.inputState, inputHapticGenerator, onInputStateUpdated, onInputEvents) {
        snapshotFlow { scope.inputState.value }
            .collect { inputState ->
                onInputStateUpdated?.invoke(inputState)
                onInputEvents?.invoke(inputEventsGenerator.onInputStateChanged(inputState))
                inputHapticGenerator.onInputStateChanged(inputState)
            }
    }
}
