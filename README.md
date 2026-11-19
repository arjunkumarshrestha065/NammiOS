# Nammi OS – V1 Prototype

Android infotainment launcher-style prototype for a Dongfeng Nammi display.

## Build
Open this folder in Android Studio (JDK 17), sync Gradle, then run:
`gradle :app:assembleDebug`

The APK output is:
`app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions
Push to `main` or manually run the workflow from the Actions tab. Download the
`NammiOS-debug-apk` artifact after a successful run.

## Prototype limitations
- Vehicle values are placeholders until an authorized OEM data interface is available.
- Camera and climate screens are placeholders; no vehicle commands are sent.
- Navigation, radio, USB media and YouTube Music are entry-point placeholders.
- Voice recognition navigates between app pages only.
- Boot launch is best-effort and may be restricted by Android/OEM firmware.
- Steering-wheel integration requires documented Android key events or OEM support.

Test while parked. Preserve factory vehicle controls and safety systems.
