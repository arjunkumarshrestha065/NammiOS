# Nammi OS – Smart EV Cockpit (starter project)

An Android 10+ infotainment starter app for a landscape display. This project is a prototype and is not an OEM vehicle integration.

## Features in this starter
- Dark cockpit-style interface with Cockpit, Navigation, Media, Camera, AC and Settings pages.
- Exit button returns to the Android system Home screen.
- Voice recognition can navigate between app pages when a speech-recognition service is available.
- Optional best-effort boot-launch preference. Android/OEM firmware may block background launches.
- Steering-wheel key-event diagnostic log. Events are observed but never consumed or remapped.

## Important limitations
- Battery, range, speed, odometer, TPMS and energy data show unavailable placeholders. No vehicle data is fabricated.
- Camera and climate pages are placeholders. They do not access the 360° camera or send AC commands.
- Navigation and USB/radio playback are not implemented in this starter.
- Steering-wheel events may not be delivered to third-party apps. Verify only while parked, and preserve all original vehicle controls.
- Do not connect to CAN bus or send undocumented commands. Use only documented OEM interfaces.

## Build with GitHub Actions
1. Create a GitHub repository named `NammiOS`.
2. Upload **all files and folders inside this ZIP**, preserving folder structure. Include the hidden `.github` folder.
3. Commit the files to the `main` branch.
4. Open the repository's **Actions** tab.
5. Open **Build Nammi OS APK**. A push to `main` starts it automatically; otherwise choose **Run workflow**.
6. When the run succeeds, open it and download the `NammiOS-debug-apk` artifact.

## Build locally
Open this folder in Android Studio with Android SDK 35 and JDK 17 installed. The GitHub workflow uses Gradle 8.9.

## Package
`com.nammios.cockpit`
