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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * The default foreground for a composite button.
 *
 * @param modifier The modifier to be applied to the foreground.
 * @param pressed A [State] that is `true` when the button is pressed.
 * @param color The color of the button when it's not pressed.
 * @param pressedColor The color of the button when it's pressed.
 */
@Composable
fun DefaultCompositeForeground(
    modifier: Modifier = Modifier,
    pressed: State<Boolean>,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    pressedColor: Color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = modifier.aspectRatio(1f),
            shape = CircleShape,
            color = if (pressed.value) pressedColor else color,
        ) { }
    }
}
