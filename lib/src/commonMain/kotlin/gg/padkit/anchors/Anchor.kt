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

package gg.padkit.anchors

import androidx.compose.ui.geometry.Offset
import kotlinx.collections.immutable.PersistentSet

/**
 * Represents an anchor point for a set of buttons.
 *
 * This is used to define the layout of the face buttons in [gg.padkit.controls.ControlFaceButtons].
 *
 * @param T The type of the button ID.
 * @property position The position of the anchor in the control's coordinate space, which is a square from (-1, -1) to (1, 1).
 * @property buttons The set of buttons associated with this anchor.
 * @property size The size of the anchor.
 */
data class Anchor<T>(
    val position: Offset,
    val buttons: PersistentSet<T>,
    val size: Float,
) {
    internal fun distance(point: Offset): Float {
        return maxOf((point - position).getDistance() - size, 0f)
    }
}
