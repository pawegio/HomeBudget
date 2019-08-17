plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.pawegio.homebudget"
        minSdkVersion(23)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.41")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.0-RC2")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.0.2")
    implementation("org.koin:koin-androidx-viewmodel:2.0.1")
    implementation("org.koin:koin-android-ext:2.0.1")
    implementation("com.google.android.gms:play-services-auth:17.0.0")
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.github.florent37:inline-activity-result-kotlin:1.0.2")
}
