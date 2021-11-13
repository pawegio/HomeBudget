import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.github.triplet.play") version "3.5.0-agp7.0"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
apply("../gradle/app-version.gradle")

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "com.pawegio.homebudget"
        minSdk = 24
        targetSdk = 30
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
}

play {
    enabled.set(System.getenv("CI") == "true")
}

tasks.withType<Test> {
    @Suppress("UnstableApiUsage")
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.4.2")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")
    implementation("com.jakewharton.rxbinding3:rxbinding:3.1.0")
    implementation("android.arch.lifecycle:extensions:1.1.1")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("org.koin:koin-core:2.2.2")
    implementation("org.koin:koin-androidx-viewmodel:2.2.2")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.github.raquezha:InlineActivityResult:1.0.0-jitpack")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")
    implementation("com.sasank.roundedhorizontalprogress:roundedhorizontalprogress:1.0.1")
    implementation("com.google.firebase:firebase-crashlytics:18.2.3")
    implementation("com.google.firebase:firebase-analytics:19.0.2")
    implementation("com.louiscad.splitties:splitties-fun-pack-android-material-components-with-views-dsl:3.0.0")
    implementation("com.louiscad.splitties:splitties-alertdialog-appcompat:3.0.0")
    implementation("com.maltaisn:calcdialog:2.2.1")

    testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("com.jraska.livedata:testing-ktx:1.1.1")
    testImplementation("com.github.langara:SMokK:0.0.4")
    testImplementation("org.threeten:threetenbp:1.4.0")
}

apply {
    plugin("com.google.gms.google-services")
}
