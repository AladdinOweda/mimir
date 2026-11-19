plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.mimir.androidapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mimir.androidapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.5.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":apps:android"))
    implementation(project(":shared:kernel"))
    implementation(project(":shared:engine:browser"))
    implementation(project(":shared:engine:computer"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}
