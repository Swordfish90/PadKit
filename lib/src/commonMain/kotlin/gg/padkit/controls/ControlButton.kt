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

package gg.padkit.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import gg.padkit.PadKitScope
import gg.padkit.handlers.ButtonPointerHandler
import gg.padkit.ids.KeyId
import gg.padkit.ui.DefaultButtonForeground
import gg.padkit.ui.DefaultControlBackground

/**
 * A composable that provides a simple button control.
 *
 * This control allows the user to input a digital value, which is represented by a [Boolean].
 * The value of the button is available in the [InputState] of the [PadKit] composable.
 *
 * @param modifier The modifier to be applied to the control.
 * @param id The [KeyId] to associate with this control.
 * @param background The composable to use as the background of the control. It receives a [State] that is `true` when the control is being pressed.
 * @param foreground The composable to use as the foreground of the control. It receives a [State] that is `true` when the control is being pressed.
 */
@Composable
fun PadKitScope.ControlButton(
    modifier: Modifier = Modifier,
    id: KeyId,
    background: @Composable (State<Boolean>) -> Unit = { DefaultControlBackground() },
    foreground: @Composable (State<Boolean>) -> Unit = { DefaultButtonForeground(pressedState = it) },
) {
    val pressedState =
        remember {
            derivedStateOf { inputState.value.getDigitalKey(id) }
        }

    val handler = remember { ButtonPointerHandler(id) }
    DisposableEffect(handler) {
        registerHandler(handler)
        onDispose {
            unregisterHandler(handler)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onGloballyPositioned { updateHandlerPosition(handler, it.boundsInRoot()) },
    ) {
        background(pressedState)
        foreground(pressedState)
    }
}
