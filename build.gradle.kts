buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.1")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}

plugins {
    kotlin("android") version "1.8.22" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
