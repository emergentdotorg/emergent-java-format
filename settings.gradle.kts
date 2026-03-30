rootProject.name = "emergent-java-format"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("java-format")
include("java-format-idea")

buildCache {
    local.isEnabled = !System.getenv().containsKey("CI")
}
