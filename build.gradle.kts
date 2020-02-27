buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-beta01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
        classpath("com.google.gms:google-services:4.3.3")
        classpath("io.fabric.tools:gradle:1.31.2")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
