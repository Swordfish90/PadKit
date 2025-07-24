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
import androidx.compose.foundation.layout.aspectRatio
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
import gg.padkit.anchors.Anchor
import gg.padkit.anchors.rememberCompositeAnchors
import gg.padkit.anchors.rememberPrimaryAnchors
import gg.padkit.handlers.FaceButtonsPointerHandler
import gg.padkit.ids.Id
import gg.padkit.inputstate.InputState
import gg.padkit.layouts.anchors.ButtonAnchorsLayout
import gg.padkit.ui.DefaultButtonForeground
import gg.padkit.ui.DefaultCompositeForeground
import gg.padkit.ui.DefaultControlBackground
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf

/**
 * A composable that provides a set of face buttons, like the ones found on a typical gamepad.
 *
 * This control allows the user to input digital values, which are represented by a [Boolean] for each button.
 * The values of the buttons are available in the [InputState] of the [PadKit] composable.
 *
 * This is an overload of [ControlFaceButtons] that takes a list of [Id.Key]s and arranges them in a circle.
 *
 * @param modifier The modifier to be applied to the control.
 * @param rotationInDegrees The rotation of the buttons in degrees.
 * @param ids The list of [Id.Key]s to associate with the buttons.
 * @param includeComposite Whether to include composite buttons, which are activated when multiple buttons are pressed at the same time.
 * @param background The composable to use as the background of the control.
 * @param foreground The composable to use as the foreground of each button. It receives the [Id.Key] of the button and a [State] that is `true` when the button is being pressed.
 * @param foregroundComposite The composable to use as the foreground of each composite button. It receives a [State] that is `true` when the composite button is being pressed.
 */
@Composable
fun PadKitScope.ControlFaceButtons(
    modifier: Modifier = Modifier,
    rotationInDegrees: Float = 0f,
    ids: PersistentList<Id.Key>,
    includeComposite: Boolean = true,
    background: @Composable () -> Unit = { DefaultControlBackground() },
    foreground: @Composable (Id.Key, State<Boolean>) -> Unit = { _, pressed ->
        DefaultButtonForeground(pressedState = pressed)
    },
    foregroundComposite: @Composable (State<Boolean>) -> Unit = { pressed ->
        DefaultCompositeForeground(pressed = pressed)
    },
) {
    val primaryAnchors = rememberPrimaryAnchors(ids, rotationInDegrees)
    val compositeAnchors =
        if (includeComposite) {
            rememberCompositeAnchors(ids, rotationInDegrees)
        } else {
            persistentListOf()
        }

    ControlFaceButtons(
        modifier = modifier,
        primaryAnchors = primaryAnchors,
        compositeAnchors = compositeAnchors,
        background = background,
        foreground = foreground,
        foregroundComposite = foregroundComposite,
    )
}

/**
 * A composable that provides a set of face buttons, like the ones found on a typical gamepad.
 *
 * This control allows the user to input digital values, which are represented by a [Boolean] for each button.
 * The values of the buttons are available in the [InputState] of the [PadKit] composable.
 *
 * This is an overload of [ControlFaceButtons] that takes a list of [Anchor]s to define the layout of the buttons.
 *
 * @param modifier The modifier to be applied to the control.
 * @param primaryAnchors The list of [Anchor]s that define the primary buttons.
 * @param compositeAnchors The list of [Anchor]s that define the composite buttons.
 * @param background The composable to use as the background of the control.
 * @param foreground The composable to use as the foreground of each button. It receives the [Id.Key] of the button and a [State] that is `true` when the button is being pressed.
 * @param foregroundComposite The composable to use as the foreground of each composite button. It receives a [State] that is `true` when the composite button is being pressed.
 */
@Composable
fun PadKitScope.ControlFaceButtons(
    modifier: Modifier = Modifier,
    primaryAnchors: PersistentList<Anchor<Id.Key>>,
    compositeAnchors: PersistentList<Anchor<Id.Key>>,
    background: @Composable () -> Unit = { DefaultControlBackground() },
    foreground: @Composable (Id.Key, State<Boolean>) -> Unit = { _, pressed ->
        DefaultButtonForeground(pressedState = pressed)
    },
    foregroundComposite: @Composable (State<Boolean>) -> Unit = { pressed ->
        DefaultCompositeForeground(pressed = pressed)
    },
) {
    val anchors = primaryAnchors + compositeAnchors
    val handler = remember(anchors) { FaceButtonsPointerHandler(anchors) }
    DisposableEffect(handler) {
        registerHandler(handler)
        onDispose {
            unregisterHandler(handler)
        }
    }

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .onGloballyPositioned { updateHandlerPosition(handler, it.boundsInRoot()) },
    ) {
        background()

        ButtonAnchorsLayout(
            modifier = Modifier.fillMaxSize(),
            anchors = primaryAnchors,
        ) {
            primaryAnchors
                .flatMap { it.buttons }
                .forEach {
                    ButtonForeground(it, inputState, foreground)
                }
        }

        ButtonAnchorsLayout(
            modifier = Modifier.fillMaxSize(),
            anchors = compositeAnchors,
        ) {
            compositeAnchors.forEach { point ->
                CompositeForeground(point.buttons, inputState, foregroundComposite)
            }
        }
    }
}

@Composable
private fun ButtonForeground(
    keyId: Id.Key,
    inputState: State<InputState>,
    content: @Composable (Id.Key, State<Boolean>) -> Unit,
) {
    val pressed =
        remember {
            derivedStateOf { inputState.value.getDigitalKey(keyId) }
        }
    content(keyId, pressed)
}

@Composable
private fun CompositeForeground(
    keys: PersistentSet<Id.Key>,
    inputState: State<InputState>,
    content: @Composable (State<Boolean>) -> Unit,
) {
    val pressed =
        remember {
            derivedStateOf { keys.all { inputState.value.getDigitalKey(it) } }
        }
    content(pressed)
}
