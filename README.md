# PadKit

PadKit is a Compose Multiplatform library that allows developers to create custom virtual gamepads for games and applications.
This project is a continuation of the original [JamPadCompose](https://github.com/piepacker/JamPadCompose) by Jam.gg.

### Controls

The following controls are currently supported:
* ControlAnalog
* ControlButton
* ControlCross
* ControlFaceButtons

### Layouts

Controls can be arranged with any composable components, but PadKit also includes the `LayoutRadial` which places a primary dial in the center, and arranges secondary dials in a circle around it.

### Usage

Include the library in your project. Check latest tag for version:

```
implementation("io.github.swordfish90:padkit:x.y.z")
```

Here's a how you can use PadKit to create a very simple gamepad layout.

```kotlin
@Composable
fun SampleGamePad() {
    PadKit(
        hapticFeedbackType = HapticFeedbackType.PRESS,
        modifier = Modifier.fillMaxSize(),
        onInputStateUpdated = {},
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            LayoutRadial(
                modifier = Modifier.padding(8.dp).weight(1f, fill = false),
                primaryDial = {
                    ControlCross(
                        modifier = Modifier.fillMaxSize(),
                        id = Id.DiscreteDirection(0),
                    )
                },
                secondaryDials = {
                    ControlButton(
                        modifier = Modifier.radialPosition(-60f),
                        id = Id.Key(0)
                    )
                },
            )
            LayoutRadial(
                modifier = Modifier.padding(8.dp).weight(1f, fill = false),
                primaryDial = {
                    ControlFaceButtons(
                        modifier = Modifier.fillMaxSize(),
                        ids =
                            listOf(1, 2)
                                .map { Id.Key(it) }
                                .toPersistentList(),
                    )
                },
                secondaryDials = {
                    ControlButton(
                        modifier = Modifier.radialPosition(-120f),
                        id = Id.Key(3)
                    )
                },
            )
        }
    }
}
```

### License

```
Copyright (c) Jam.gg 2024.
Copyright (c) Filippo Scognamiglio 2025.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```