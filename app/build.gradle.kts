plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.nammios.cockpit"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.nammios.cockpit"
        minSdk = 29
        targetSdk = 32
        versionCode = 4
        versionName = "4.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
