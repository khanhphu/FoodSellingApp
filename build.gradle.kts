buildscript {
    repositories {
        google() // Google's Maven repository
        mavenCentral() // Maven Central repository
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2") // Ensure this is up to date
        classpath("com.google.gms:google-services:4.4.2")
    }

}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id ("com.google.gms.google-services") version "4.4.2" apply false
}
