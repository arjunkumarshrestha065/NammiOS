# Nammi OS — V1 Prototype

A landscape Android 10 starter project for the Dongfeng Nammi infotainment display.

## Included
- Dark navy / electric-blue Compose UI designed for a 1024x600 landscape screen.
- Cockpit layout with deliberately blank vehicle readings (no fabricated BMS/TPMS data).
- Exit button that launches the system's default HOME activity.
- Settings toggle for a best-effort boot launch attempt.
- Voice recognition for navigating app pages (Cockpit, Navigation, Radio, USB Media, YouTube Music, Camera, AC, Settings).
- Camera and climate placeholder pages with clear OEM-integration status.

## Build
1. Open this folder in Android Studio (JDK 17 recommended).
2. Let Android Studio sync Gradle and download dependencies.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

This project does not include the Gradle wrapper JAR. Open it through Android Studio, or generate wrapper files from Android Studio's Gradle tooling.

## Important integration limits
- The 360-degree camera needs an OEM camera API, documented feed, or approved video interface. The app cannot access a camera merely because the original screen displays it.
- AC controls need a documented OEM climate API. This prototype does not send vehicle commands.
- Steering-wheel key events must be tested on the actual head unit before mapping any buttons.
- Android 10 background-start restrictions may prevent the boot receiver from opening the app automatically. The setting records the preference but does not override system restrictions.
- No speed, battery, range, odometer, TPMS or energy values are read from the vehicle in this prototype.
- Test while parked and keep the original vehicle controls accessible.


## GitHub Actions APK build
This project includes `.github/workflows/build-apk.yml`.
After pushing the project to the `main` branch, GitHub Actions builds a debug APK.
You can also run it manually from the repository's Actions tab using **Build Nammi OS APK**.
Download `NammiOS-debug-apk` from the completed workflow run's Artifacts section.
