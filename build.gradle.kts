buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath(kotlin("gradle-plugin", version = "1.8.10"))
        classpath ("gradle.plugin.com.onesignal:onesignal-gradle-plugin:0.14.0")
    }
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks.register("clean",Delete::class) {
    delete(rootProject.buildDir)
}
