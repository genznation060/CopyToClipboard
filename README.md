# QuickCopy — Android "Share to Clipboard" App

An ultra-fast Android utility that registers into the native Android Share Sheet as **"Copy to Clipboard"**.

## How It Works

1. Open any app on your phone (Chrome, Firefox, Twitter/X, YouTube, Instagram, Reddit, Notes…).
2. Tap **Share**.
3. Select **"Copy to Clipboard"** (QuickCopy).
4. QuickCopy extracts the text/URL, strips tracking query parameters (`utm_*`, `fbclid`, `si`, etc.), copies it directly into Android's Clipboard, shows a brief Toast, and closes immediately.

## Features

- Appears in the system Share Sheet
- Automatically cleans tracking parameters from URLs
- Saves history of shared items (last 100)
- Manual test copy from the app
- Haptic feedback + Toast confirmation
- Material 3 + Jetpack Compose UI

## Requirements

- minSdk 26 (Android 8.0+)
- targetSdk 35

## How to Build

### Option A — Android Studio (PC)

1. Open the project folder in Android Studio (Hedgehog or newer).
2. Let Gradle sync.
3. Click **Run** on a connected device or emulator,  
   **or**  
   Build → Build Bundle(s) / APK(s) → Build APK(s).

### Option B — GitHub Actions (no PC needed after upload)

1. Create a new repository on GitHub.
2. Upload **all** files from this project (including the `.github` folder).
3. Go to the **Actions** tab → select **Build APK** → **Run workflow**.
4. When the job finishes, download the **QuickCopy-Debug-APK** artifact.
5. Transfer the APK to your phone and install it (enable “Install from unknown sources”).

## Project Structure

```
QuickCopy/
├── .github/workflows/build-apk.yml   ← GitHub Actions workflow
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/quickcopy/app/
│       │   ├── MainActivity.kt
│       │   ├── ShareReceiverActivity.kt
│       │   ├── data/ClipboardRepository.kt
│       │   ├── util/UrlCleaner.kt
│       │   └── ui/theme/…
│       └── res/…
├── gradle/
├── gradlew / gradlew.bat
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## License

This is a sample/utility project. Use and modify freely.
