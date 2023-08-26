import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("com.github.triplet.play") version "3.7.0"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
apply("../gradle/app-version.gradle")

android {
    namespace = "com.pawegio.homebudget"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.pawegio.homebudget"
        minSdk = 26
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            val localProperties = Properties()
            if (project.rootProject.file("local.properties").exists()) {
                localProperties.load(project.rootProject.file("local.properties").inputStream())
            }
            storeFile = file("../upload.jks")
            storePassword = localProperties.getOrDefault("storePassword", "\"\"") as String
            keyAlias = localProperties.getOrDefault("keyAlias", "\"\"") as String
            keyPassword = localProperties.getOrDefault("keyPassword", "\"\"") as String
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.contracts.ExperimentalContracts",
            "-Xopt-in=splitties.experimental.InternalSplittiesApi",
            "-Xopt-in=splitties.experimental.ExperimentalSplittiesApi"
        )
    }
    buildFeatures {
        viewBinding = true
    }
}

play {
    enabled.set(System.getenv("CI") == "true")
}

tasks.withType<Test> {
    @Suppress("UnstableApiUsage")
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.6.4")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")
    implementation("com.jakewharton.rxbinding3:rxbinding:3.1.0")
    implementation("android.arch.lifecycle:extensions:1.1.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("org.koin:koin-core:2.2.2")
    implementation("org.koin:koin-androidx-viewmodel:2.2.2")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.github.raquezha:InlineActivityResult:1.0.0-jitpack")
    implementation("com.sasank.roundedhorizontalprogress:roundedhorizontalprogress:1.0.1")
    implementation("com.google.firebase:firebase-crashlytics:18.3.6")
    implementation("com.google.firebase:firebase-analytics:21.2.2")
    implementation("com.louiscad.splitties:splitties-fun-pack-android-material-components-with-views-dsl:3.0.0")
    implementation("com.louiscad.splitties:splitties-alertdialog-appcompat:3.0.0")
    implementation("com.maltaisn:calcdialog:2.2.1")
    implementation("com.google.guava:guava:27.0.1-android")

    testImplementation("io.kotest:kotest-runner-junit5:5.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("com.jraska.livedata:testing-ktx:1.1.1")
    testImplementation("com.github.langara:SMokK:0.0.4")
}

apply {
    plugin("com.google.gms.google-services")
}
