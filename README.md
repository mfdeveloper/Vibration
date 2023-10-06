# Vibration/Haptics: Unity Plugin

<h1 align="left">
    <img src="./Images/haptic-logo-color.png">
</h1>

[![openupm](https://img.shields.io/npm/v/com.benoitfreslon.vibration?label=openupm&registry_uri=https://package.openupm.com)](https://openupm.com/packages/com.benoitfreslon.vibration/)

>**PS:** This is a fork of original Vibration package **[BenoitFreslon/Vibration](https://github.com/BenoitFreslon/Vibration)** to be published in [openupm](https://openupm.com) registry, while the changes aren't merged into it!

Native **free** plugin for Unity for Android, iOS and [WebGL](https://caniuse.com/webgl2) (with some limitations).
Use custom vibrations/haptics on mobile.

If you like this free plugin, that's be cool if you can buy me a coffee 😀☕️
Send tips to <https://paypal.me/UnityVibrationPlugin>

## Supported Platforms

- <img src="./Images/Icons/android-icon.png" alt="Aimeos logo" title="Android" align="center" height="32" /> Android
- <img src="./Images/Icons/ios-icon.png" alt="Aimeos logo" title="iOS" align="center" height="32" /> iOS
- <img src="./Images/Icons/webgl-icon.png" alt="Aimeos logo" title="iOS" align="center" height="32" /> <a href="https://caniuse.com/webgl2"> WebGL </a> (some limitations apply on Mobile)

## Installation

### OpenUPM

Install the [OpenUPM](https://openupm.com) CLI and add the [com.benoitfreslon.vibration](https://openupm.com/packages/com.benoitfreslon.vibration) package

```bash

# Install openupm-cli
npm install -g openupm-cli

# Go to your Unity project directory
cd YOUR_UNITY_PROJECT_DIR

# Install package: com.benoitfreslon.vibration
openupm add com.benoitfreslon.vibration

```

Or alternatively, merge the snippet below to [Packages/manifest.json](https://docs.unity3d.com/Manual/upm-manifestPrj.html) manually

```jsonc
{
    // Verifiy which is the latest tag in this repository
    "dependencies": {
        "com.benoitfreslon.vibration": "<LATEST_VERSION>"
    },
    // If you already have the openupm registry,
    // only add this package to "scopes" array
    "scopedRegistries": [
        {
            "name": "package.openupm.com",
            "url": "https://package.openupm.com",
            "scopes": [
                "com.benoitfreslon.vibration"
            ]
        }
    ]
}
```

### Github package

1. Add a **`$HOME/.upmconfig.toml`** file with the TOKEN to authenticate on registry

    ```toml
    [npmAuth."https://npm.pkg.github.com/@mfdeveloper"]
    # Generate the token from your github profile:
    # https://github.com/settings/tokens
    _authToken = "<TOKEN-VALUE>"
    email = "<YOUR_EMAIL>"
    alwaysAuth = true
    ```

    > **WARNING:** _GitHub/Gitlab_ Packages registry always requires authentication. For **_private_** and **_public_** packages.

2. Configure the scope **registry** and **dependency** in your Unity project `Packages/manifest.json`

    ```jsonc
    {
        "dependencies": {
            // Verifiy which is the latest tag in this repository
            "com.benoitfreslon.vibration": "<LATEST_VERSION>"
        },
        "scopedRegistries": [
            {
                "name": "Github Packages: mfdeveloper",
                "url": "https://npm.pkg.github.com/@mfdeveloper",
                "scopes": [
                    "com.benoitfreslon"
                ]
            }
        ]
    }
    ```

### Git dependency

The minimal checked Unity Version is **`2019.3.*`** LTS

Open Package Manager and "Add package from git url..." using next string:

- `https://github.com/BenoitFreslon/Vibration.git#upm`

Or use the latest git release/tag:

- `https://github.com/BenoitFreslon/Vibration.git#<LATEST_VERSION>`

You also can edit `Packages/manifest.json` manually, just add:

- `"com.benoitfreslon.vibration": "https://github.com/BenoitFreslon/Vibration.git#<LATEST_VERSION>",`

Or you can simply copy and paste the entire `[upm]` branch content from this repo, to your Unity3D `Packages/com.benoitfreslon.vibration` folder.

## Getting Started

There are 2 ways to use this plugin:

1. Use the `Runtime/VibrationComponent.cs` script attached to a _gameObject_ **(Recommended)**

    ![Vibration Component](./Images/vibration-component-inspector.png)

    On that script, you can:

    - Enable/Disable vibration from inspector or programatically
        > **TIP:** Useful for enable/disable from a menu settings in your game!
    - Add a `ScriptableObject` asset with vibration settings (only enable/disable for now)
    - Configure the duration and/or select a pre-defined vibration effect type

    This `MonoBehaviour` component use `Runtime/Vibration.cs` static class as a "_fallback_" for some implemented native integrations (**IOS** and **WebGL**)

2. Use the `Runtime/Vibration.cs` static class

    See the scene and a sample `MonoBehaviour` script under folder: `Samples/VibrationExample`

## Vibrations

Using `Runtime/VibrationComponent`

```csharp
// That's the main method to pass a duration (milliseconds)
// and/or a pre-defined `VibrationType` effect
Vibrate(
    milliseconds: 20, 
    vibrationType: VibrationType.Click
);

```

Optionally, you can pass an array of values of **_pattern_** as well:

```csharp
// That's the main method to pass a duration (milliseconds)
// and/or a pre-defined `VibrationType` effect
Vibrate(
    pattern: new[] { 200, 10, 50 }, 
    repeat: VibrationRepeat.Once
);

```

Also, it's possible define the **timeunit** of the duration value:

```csharp
// That's the main method to pass a duration (milliseconds)
// and/or a pre-defined `VibrationType` effect
Vibrate(
    duration: 10, 
    timeUnit: MobileTimeUnit.Seconds
);

```

Check if the mobile device has **vibration support**:

```csharp
// Fallback to "Vibration.HasVibrator()" on iOS and WebGL
HasVibrator();
```

### Android (only)

Check if an Android [VibrationEffect](https://developer.android.com/reference/android/os/VibrationEffect) is supported:

```csharp
// Where: "0" is `VibrationEffect.EFFECT_CLICK` value 
// from native Android Kotlin/Java
IsEffectSupported(0);
```

Check if an Android [VibrationEffect.Composition](https://developer.android.com/reference/android/os/VibrationEffect.Composition) is supported:

```csharp
// Where: "1" is `VibrationEffect.Composition.PRIMITIVE_CLICK` value 
// from native Android Kotlin/Java
IsPrimitiveSupported(1);
```

### iOS and Android

Using `Runtime/Vibration.cs` static class

#### Default vibration

Use `Vibration.Vibrate();` for a classic default ~400ms vibration

#### Pop vibration

Pop vibration: weak boom (For iOS: only available with the haptic engine. iPhone 6s minimum or Android)

`Vibration.VibratePop();`

#### Peek Vibration

Peek vibration: strong boom (For iOS: only available on iOS with the haptic engine. iPhone 6s minimum or Android)

`Vibration.VibratePeek();`

#### Nope Vibration

Nope vibration: series of three weak booms (For iOS: only available with the haptic engine. iPhone 6s minimum or Android)

`Vibration.VibrateNope();`

---

## Android Only

#### Custom duration in milliseconds

`Vibration.Vibrate(500);`

#### Pattern

```csharp
long[] pattern = { 0, 1000, 1000, 1000, 1000 };
Vibration.Vibrate (pattern, -1);
```

#### Cancel

Using `Runtime/VibrationComponent.cs`

```csharp

// Cancel for Android and WebGL
VibrationComponent.Cancel();
```

Using `Runtime/Vibration.cs` static class

```csharp
Vibration.Cancel();
```

---

## IOS only

vibration using haptic engine

`Vibration.VibrateIOS(ImpactFeedbackStyle.Light);`

`Vibration.VibrateIOS(ImpactFeedbackStyle.Medium);`

`Vibration.VibrateIOS(ImpactFeedbackStyle.Heavy);`

`Vibration.VibrateIOS(ImpactFeedbackStyle.Rigid);`

`Vibration.VibrateIOS(ImpactFeedbackStyle.Soft);`

`Vibration.VibrateIOS(NotificationFeedbackStyle.Error);`

`Vibration.VibrateIOS(NotificationFeedbackStyle.Success);`

`Vibration.VibrateIOS(NotificationFeedbackStyle.Warning);`

`Vibration.VibrateIOS_SelectionChanged();`

## Development

### Publish (Github Packages)

1. Generate a [Github access token (classic)](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic)

2. Authenticate using the generated TOKEN:

   Create a user `$HOME/.npmrc` file

    ```ini
    # Where: <GITHUB_TOKEN> is the access token (classic) generated in your profile
    @github:registry=https://npm.pkg.github.com
    //npm.pkg.github.com/:_authToken=<GITHUB_TOKEN>
    ```

    Or, use the file `Packages/com.benoitfreslon.vibration/.npmrc` that already exists in this repository:

    ```bash
    # Export the environment variable "$GITHUB_TOKEN"
    # the file ".npmrc" inside of this repo will use this variable
    export GITHUB_TOKEN=<GITHUB_TOKEN>
    ```

3. Publish the package with [npm publish](https://docs.npmjs.com/cli/v9/commands/npm-publish)

    From [`[upm-package-embedded]`](https://github.com/mfdeveloper/Vibration/tree/upm-package-embedded) git branch

    ```bash
    # Clone this repo and checkout to branch "[upm-package-embedded]"
    git clone https://github.com/mfdeveloper/Vibration.git
    git checkout upm-package-embedded

    # Run the npm scripts
    npm install
    npm run publish:package # Publish from repository ROOT path

    cd Packages/com.benoitfreslon.vibration 
    npm publish # Publish from package path
    ``````

### Publish (OpenUPM)

1. Bump the version with a new Github release or Git tag
2. The changes should be reflected in: [https://openupm.com/packages/com.benoitfreslon.vibration](https://openupm.com/packages/com.benoitfreslon.vibration)

    > **See:** [Modifying UPM Package](https://openupm.com/docs/modifying-upm-package.html#modifying-upm-package)

## References

### ANDROID

- [Using Vibrate In Android](https://proandroiddev.com/using-vibrate-in-android-b0e3ef5d5e07)
- [Android 12: VibratorManager & New Vibration Primitives](https://yggr.medium.com/exploring-android-12-vibratormanager-new-vibration-primitives-e862c95fe938)
- [Developers Android: VibrationEffect](https://developer.android.com/reference/android/os/VibrationEffect)
- [Developers Android: VibrationEffect.Composition](https://developer.android.com/reference/android/os/VibrationEffect.Composition)

### OPENUPM

- [Adding UPM Package](https://openupm.com/docs/adding-upm-package.html)
- [Modifying UPM Package](https://openupm.com/docs/modifying-upm-package.html#modifying-upm-package)

### GITHUB PACKAGES

- [Custom Package with Git Dependencies](https://forum.unity.com/threads/custom-package-with-git-dependencies.628390/)
- [Using GitHub Packages Registry with Unity Package Manager](https://forum.unity.com/threads/using-github-packages-registry-with-unity-package-manager.861076)
- [Package manager not displaying all packages in scoped registry](https://forum.unity.com/threads/package-manager-not-displaying-all-packages-in-scoped-registry.791598/#post-5666161)

#### ICONS (Copyright)

<a href="https://www.flaticon.com/free-icons/haptic" title="haptic icons">Haptic icons created by Uniconlabs - Flaticon</a>
