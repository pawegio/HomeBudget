buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.4.0")
        classpath("com.google.gms:google-services:4.5.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.8")
    }
}

plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.4.10" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
