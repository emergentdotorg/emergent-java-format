rootProject.name = "java-format-idea"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


buildCache {
    local.isEnabled = !System.getenv().containsKey("CI")
}
