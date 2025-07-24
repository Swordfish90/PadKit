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

package gg.padkit.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import gg.padkit.anchors.rememberCompositeAnchors
import gg.padkit.anchors.rememberPrimaryAnchors
import gg.padkit.handlers.CrossPointerHandler
import gg.padkit.layouts.anchors.ButtonAnchorsLayout
import gg.padkit.utils.ifUnspecified
import kotlinx.collections.immutable.toPersistentList

/**
 * The default foreground for a cross control.
 *
 * @param modifier The modifier to be applied to the foreground.
 * @param directionState A [State] that contains the current [Offset] of the control.
 * @param rightDial The composable to use for the right directional button.
 * @param bottomDial The composable to use for the bottom directional button.
 * @param leftDial The composable to use for the left directional button.
 * @param topDial The composable to use for the top directional button.
 * @param foregroundComposite The composable to use for the composite buttons (diagonals).
 * @param allowDiagonals Whether to allow diagonal input.
 */
@Composable
fun DefaultCrossForeground(
    modifier: Modifier = Modifier,
    directionState: State<Offset>,
    rightDial: @Composable (State<Boolean>) -> Unit = {
        DefaultButtonForeground(
            pressedState = it,
            iconPainter = rememberVectorPainter(Icons.Default.KeyboardArrowRight),
        )
    },
    bottomDial: @Composable (State<Boolean>) -> Unit = {
        DefaultButtonForeground(
            pressedState = it,
            iconPainter = rememberVectorPainter(Icons.Default.KeyboardArrowDown),
        )
    },
    leftDial: @Composable (State<Boolean>) -> Unit = {
        DefaultButtonForeground(
            pressedState = it,
            iconPainter = rememberVectorPainter(Icons.Default.KeyboardArrowLeft),
        )
    },
    topDial: @Composable (State<Boolean>) -> Unit = {
        DefaultButtonForeground(
            pressedState = it,
            iconPainter = rememberVectorPainter(Icons.Default.KeyboardArrowUp),
        )
    },
    foregroundComposite: @Composable (State<Boolean>) -> Unit = {
        DefaultCompositeForeground(pressed = it)
    },
    allowDiagonals: Boolean = true,
) {
    val directions = CrossPointerHandler.Direction.entries.toPersistentList()
    val primaryAnchors = rememberPrimaryAnchors(directions, 0f)

    ButtonAnchorsLayout(
        modifier = modifier.fillMaxSize(),
        anchors = primaryAnchors,
    ) {
        DirectionalButton(directionState, { it.x > 0.5f }, rightDial)
        DirectionalButton(directionState, { it.y < -0.5f }, bottomDial)
        DirectionalButton(directionState, { it.x < -0.5f }, leftDial)
        DirectionalButton(directionState, { it.y > 0.5f }, topDial)
    }

    val compositeAnchors = rememberCompositeAnchors(directions, 0f)

    if (allowDiagonals) {
        ButtonAnchorsLayout(
            modifier = modifier.fillMaxSize(),
            anchors = compositeAnchors,
        ) {
            DirectionalButton(directionState, { it.y < -0.5f && it.x > 0.5f }, foregroundComposite)
            DirectionalButton(directionState, { it.y < -0.5f && it.x < -0.5f }, foregroundComposite)
            DirectionalButton(directionState, { it.y > 0.5f && it.x < -0.5f }, foregroundComposite)
            DirectionalButton(directionState, { it.y > 0.5f && it.x > 0.5f }, foregroundComposite)
        }
    }
}

@Composable
private fun DirectionalButton(
    directionState: State<Offset>,
    check: (Offset) -> Boolean,
    content: @Composable (State<Boolean>) -> Unit,
) {
    val isPressed =
        remember {
            derivedStateOf { check(directionState.value.ifUnspecified { Offset.Zero }) }
        }
    content(isPressed)
}
