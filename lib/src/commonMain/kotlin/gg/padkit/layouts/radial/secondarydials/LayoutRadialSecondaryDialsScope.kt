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

package gg.padkit.layouts.radial.secondarydials

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.ui.Modifier

/**
 * A scope for composables that can be placed in a circle around a central composable.
 *
 * @see gg.padkit.layouts.radial.LayoutRadial
 */
@LayoutScopeMarker
class LayoutRadialSecondaryDialsScope {
    /**
     * Sets the position of the composable in the circle, in degrees.
     *
     * @param angleInDegrees The angle in degrees, where 0 is to the right, 90 is up, 180 is to
     * the left, and 270 is down.
     */
    fun Modifier.radialPosition(angleInDegrees: Float) =
        this.then(
            LayoutRadialSecondaryDialPositionModifier(angleInDegrees),
        )

    /**
     * Sets the scale of the composable relative to the other secondary dials.
     *
     * @param scale The scale of the composable.
     */
    fun Modifier.radialScale(scale: Float) =
        this.then(
            LayoutRadialSecondaryDialScaleModifier(scale),
        )
}
