pluginManagement {
    repositories {
        gradlePluginPortal() // Still allow resolution from the default portal
        mavenLocal() // Add local Maven repository for plugin resolution
    }
}

rootProject.name = "emergent-java-format"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("java-format")
include("java-format-idea")

buildCache {
    local.isEnabled = true // !System.getenv().containsKey("CI")
}
