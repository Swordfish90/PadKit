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

package gg.padkit.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator

internal object IosHapticGenerator : HapticGenerator {
    private val impactFeedbackGenerator = UIImpactFeedbackGenerator()

    override fun generate(type: HapticEffect) {
        when (type) {
            HapticEffect.PRESS -> impactFeedbackGenerator.impactOccurredWithIntensity(0.5)
            HapticEffect.RELEASE -> impactFeedbackGenerator.impactOccurredWithIntensity(0.3)
        }
    }
}

@Composable
internal actual fun rememberHapticGenerator(): HapticGenerator {
    return remember { IosHapticGenerator }
}
