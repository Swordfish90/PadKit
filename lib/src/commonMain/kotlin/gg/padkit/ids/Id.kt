/*
 * Copyright (c) Jam.gg 2025.
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

package gg.padkit.ids

import kotlin.jvm.JvmInline

/**
 * A sealed interface for all control IDs.
 *
 * This is used to identify controls in the [gg.padkit.PadKit] composable.
 *
 * @see Key
 * @see ContinuousDirection
 * @see DiscreteDirection
 */
sealed interface Id {
    /**
     * An ID for a digital control, like a button.
     *
     * @param value The integer value of the ID.
     */
    @JvmInline
    value class Key(val value: Int) : Id

    /**
     * An ID for a continuous direction control, like an analog stick.
     *
     * @param value The integer value of the ID.
     */
    @JvmInline
    value class ContinuousDirection(val value: Int) : Id

    /**
     * An ID for a discrete direction control, like a D-pad.
     *
     * @param value The integer value of the ID.
     */
    @JvmInline
    value class DiscreteDirection(val value: Int) : Id
}
