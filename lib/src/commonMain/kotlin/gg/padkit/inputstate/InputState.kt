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

package gg.padkit.inputstate

import androidx.compose.ui.geometry.Offset
import gg.padkit.ids.ContinuousDirectionId
import gg.padkit.ids.DiscreteDirectionId
import gg.padkit.ids.KeyId
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * Represents the state of the gamepad at a given time.
 *
 * It contains the state of all the controls, including digital keys, continuous directions, and discrete directions.
 *
 * @property digitalKeys The state of the digital keys. The set contains the [KeyId]s of the keys that are currently pressed.
 * @property continuousDirections The state of the continuous directions. The map contains the [ContinuousDirectionId]s of the controls that are currently being touched, and their corresponding [Offset]s.
 * @property discreteDirections The state of the discrete directions. The map contains the [DiscreteDirectionId]s of the controls that are currently being touched, and their corresponding [Offset]s.
 */
data class InputState(
    internal val digitalKeys: PersistentSet<Int> = persistentSetOf(),
    internal val continuousDirections: PersistentMap<Int, Offset> = persistentMapOf(),
    internal val discreteDirections: PersistentMap<Int, Offset> = persistentMapOf(),
) {
    /**
     * Sets the state of a digital key.
     *
     * @param digitalId The [KeyId] of the key to set.
     * @param value The new state of the key. `true` if pressed, `false` otherwise.
     * @return A new [InputState] with the updated key state.
     */
    fun setDigitalKey(
        digitalId: KeyId,
        value: Boolean,
    ): InputState {
        return if (value) {
            copy(digitalKeys = digitalKeys.add(digitalId.value))
        } else {
            copy(digitalKeys = digitalKeys.remove(digitalId.value))
        }
    }

    /**
     * Gets the state of a digital key.
     *
     * @param digitalId The [KeyId] of the key to get.
     * @return `true` if the key is pressed, `false` otherwise.
     */
    fun getDigitalKey(digitalId: KeyId): Boolean {
        return digitalKeys.contains(digitalId.value)
    }

    /**
     * Sets the state of a continuous direction control.
     *
     * @param continuousDirectionId The [ContinuousDirectionId] of the control to set.
     * @param offset The new [Offset] of the control.
     * @return A new [InputState] with the updated control state.
     */
    fun setContinuousDirection(
        continuousDirectionId: ContinuousDirectionId,
        offset: Offset,
    ): InputState {
        return if (offset == Offset.Unspecified) {
            copy(continuousDirections = continuousDirections.remove(continuousDirectionId.value))
        } else {
            copy(continuousDirections = continuousDirections.put(continuousDirectionId.value, offset))
        }
    }

    /**
     * Gets the state of a continuous direction control.
     *
     * @param continuousDirectionId The [ContinuousDirectionId] of the control to get.
     * @param default The default [Offset] to return if the control is not being touched.
     * @return The [Offset] of the control, or [default] if the control is not being touched.
     */
    fun getContinuousDirection(
        continuousDirectionId: ContinuousDirectionId,
        default: Offset = Offset.Unspecified,
    ): Offset {
        return continuousDirections.getOrElse(continuousDirectionId.value) { default }
    }

    /**
     * Sets the state of a discrete direction control.
     *
     * @param discreteDirectionId The [DiscreteDirectionId] of the control to set.
     * @param offset The new [Offset] of the control.
     * @return A new [InputState] with the updated control state.
     */
    fun setDiscreteDirection(
        discreteDirectionId: DiscreteDirectionId,
        offset: Offset,
    ): InputState {
        return if (offset == Offset.Unspecified) {
            copy(discreteDirections = discreteDirections.remove(discreteDirectionId.value))
        } else {
            copy(discreteDirections = discreteDirections.put(discreteDirectionId.value, offset))
        }
    }

    /**
     * Gets the state of a discrete direction control.
     *
     * @param discreteDirectionId The [DiscreteDirectionId] of the control to get.
     * @param default The default [Offset] to return if the control is not being touched.
     * @return The [Offset] of the control, or [default] if the control is not being touched.
     */
    fun getDiscreteDirection(
        discreteDirectionId: DiscreteDirectionId,
        default: Offset = Offset.Unspecified,
    ): Offset {
        return discreteDirections.getOrElse(discreteDirectionId.value) { default }
    }
}
