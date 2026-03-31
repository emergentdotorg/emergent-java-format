plugins {
    id("java")
    id("nebula.release")
}

group = providers.gradleProperty("pluginGroup").get()
//version = providers.gradleProperty("pluginVersion").get()
val javaFormatVersion = providers.gradleProperty("javaFormatVersion").get()

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}
