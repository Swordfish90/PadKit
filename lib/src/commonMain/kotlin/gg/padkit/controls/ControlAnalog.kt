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

package gg.padkit.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import gg.padkit.PadKitScope
import gg.padkit.handlers.AnalogPointerHandler
import gg.padkit.ids.Id
import gg.padkit.ui.DefaultButtonForeground
import gg.padkit.ui.DefaultControlBackground
import gg.padkit.utils.GeometryUtils
import gg.padkit.utils.ifUnspecified

/**
 * A composable that provides an analog stick control.
 *
 * This control allows the user to input a continuous direction, which is represented by an [Offset].
 * The value of the analog stick is available in the [InputState] of the [PadKit] composable.
 *
 * @param modifier The modifier to be applied to the control.
 * @param id The [Id.ContinuousDirection] to associate with this control.
 * @param analogPressId The [Id.Key] to associate with the press state of the analog stick.
 * If this is not null, the digital key with this id will be set to `true` when the analog stick is being touched.
 * @param foregroundSize The size of the foreground relative to the background.
 * @param background The composable to use as the background of the control.
 * @param foreground The composable to use as the foreground of the control. It receives a [State] that is `true` when the control is being touched.
 */
@Composable
fun PadKitScope.ControlAnalog(
    modifier: Modifier = Modifier,
    id: Id.ContinuousDirection,
    analogPressId: Id.Key? = null,
    foregroundSize: Float = 0.66f,
    background: @Composable () -> Unit = { DefaultControlBackground() },
    foreground: @Composable (State<Boolean>) -> Unit = {
        DefaultButtonForeground(pressedState = it, scale = 1f)
    },
) {
    val handler = remember { AnalogPointerHandler(id, analogPressId) }
    DisposableEffect(handler) {
        registerHandler(handler, AnalogPointerHandler.Data())
        onDispose {
            unregisterHandler(handler)
        }
    }

    BoxWithConstraints(
        modifier =
            modifier
                .aspectRatio(1f)
                .onGloballyPositioned { updateHandlerPosition(handler, it.boundsInRoot()) },
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            background()
        }

        val positionState =
            remember {
                derivedStateOf { inputState.value.getContinuousDirection(id) }
            }

        val pressedState =
            remember {
                derivedStateOf { inputState.value.getContinuousDirection(id) != Offset.Unspecified }
            }

        val position = positionState.value
        val safePosition =
            GeometryUtils.mapSquareToCircle(
                position.ifUnspecified { Offset.Zero },
            )

        Box(
            modifier =
                Modifier
                    .fillMaxSize(foregroundSize)
                    .offset(
                        maxWidth * safePosition.x * 0.25f,
                        -maxHeight * safePosition.y * 0.25f,
                    ),
        ) {
            foreground(pressedState)
        }
    }
}
