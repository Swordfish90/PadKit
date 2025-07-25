# PadKit

PadKit is a Compose Multiplatform library that allows developers to create custom virtual gamepads for games and applications.

### Controls

The following controls are currently supported:
* ControlAnalog
* ControlButton
* ControlCross
* ControlFaceButtons

### Usage

Include the library in your project. Check latest tag for version:

```
implementation("io.github.swordfish90:padkit:x.y.z")
```

Here's a how you can use PadKit to create a very simple gamepad layout.

```kotlin
@Composable
private fun MyGamePad() {
   PadKit(
      modifier = Modifier.fillMaxSize().aspectRatio(2f),
      onInputStateUpdated = { }
   ) {
      Row(modifier = Modifier.fillMaxSize()) {
         ControlCross(
            modifier = Modifier.weight(1f),
            id = 0
         )
         ControlFaceButtons(
            modifier = Modifier.weight(1f),
            ids = listOf(1, 2, 3)
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
