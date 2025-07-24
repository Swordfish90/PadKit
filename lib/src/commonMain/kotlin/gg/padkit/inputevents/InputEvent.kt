/*
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

package gg.padkit.inputevents

import androidx.compose.ui.geometry.Offset

/**
 * Represents a single input event.
 *
 * This is used to notify the user of changes in the input state.
 *
 * @see ContinuousDirection
 * @see DiscreteDirection
 * @see Button
 */
sealed interface InputEvent {
    /**
     * An event for a continuous direction control.
     *
     * @param id The ID of the control.
     * @param direction The new direction of the control.
     */
    data class ContinuousDirection(val id: Int, val direction: Offset) : InputEvent

    /**
     * An event for a discrete direction control.
     *
     * @param id The ID of the control.
     * @param direction The new direction of the control.
     */
    data class DiscreteDirection(val id: Int, val direction: Offset) : InputEvent

    /**
     * An event for a button control.
     *
     * @param id The ID of the control.
     * @param pressed `true` if the button is pressed, `false` otherwise.
     */
    data class Button(val id: Int, val pressed: Boolean) : InputEvent
}
