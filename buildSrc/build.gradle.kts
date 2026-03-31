plugins {
    //id("java")
    //`java-library`
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    //mavenCentral()
}

dependencies {
    implementation("nebula.release:nebula.release.gradle.plugin:21.0.0")
    //implementation("com.bmuschko:gradle-docker-plugin:6.4.0")
}