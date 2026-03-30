import com.github.javaparser.printer.concretesyntaxmodel.CsmElement.token
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    //id("java") // Java support
    java
    //`java-library`
    kotlin("jvm") version "2.3.20"
    //id("org.jetbrains.kotlin.jvm") version "2.1.20"
    //id("org.jetbrains.intellij.platform") version "2.10.2"
    id("org.jetbrains.intellij.platform") version "2.13.1"

    //alias(libs.plugins.kotlin) // Kotlin support
    //alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()
val javaFormatVersion = providers.gradleProperty("javaFormatVersion").get()

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}


// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(21)
    //compilerOptions {
    //    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    //}
}

// Configure project's dependencies
repositories {
    mavenCentral()
    mavenLocal()

    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/version_catalogs.html
dependencies {
    implementation("org.emergent.javaformat:java-format:${javaFormatVersion}")
    implementation("org.immutables:value:2.12.1")
    annotationProcessor("org.immutables:value:2.12.1")


    //implementation("org.emergent.javaformat:emergent-java-format-jdk-bootstrap:${javaFormatVersion}")
    //implementation("com.palantir.sls.versions:sls-versions:1.11.0")
    //implementation("com.github.zafarkhaja:java-semver:0.10.2")
    //implementation("org.jspecify:jspecify:1.0.0")
    // https://mvnrepository.com/artifact/junit/junit
    //testImplementation("org.emergent.javaformat:java-format:${javaFormatVersion}:tests")
    //testImplementation("junit:junit:4.13.2")
    // https://mvnrepository.com/artifact/com.google.truth/truth
    //testImplementation("com.google.truth:truth:1.4.5")
    //testImplementation("org.assertj:assertj-core:3.27.7")
    //testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.2")
    //testImplementation("org.junit.jupiter:junit-jupiter-migrationsupport:5.14.2")
    //testImplementation("org.junit.jupiter:junit-jupiter:5.14.2")
    //implementation(kotlin("stdlib-jdk8"))

    //testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //testImplementation("org.junit.jupiter:junit-jupiter")
    //testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.javaSemver)
    implementation(libs.jspecify)
    testImplementation(libs.assertjCore)
    testImplementation(libs.junit)
    testImplementation(libs.junitPlatformLaucnher)
    testImplementation(libs.opentest4j)
    testImplementation(libs.truth)
    testImplementation(libs.bundles.javaFormat)
    testImplementation(libs.bundles.junitJupiter)

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        //intellijIdea("2025.2.4")
        intellijIdea(providers.gradleProperty("platformVersion"))
        // Add plugin dependencies for compilation here:
        //bundledPlugin("com.intellij.java")
        //bundledPlugin("org.jetbrains.kotlin")

        //intellijIdea(providers.gradleProperty("platformVersion"))
        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        // Module Dependencies. Uses `platformBundledModules` property from the gradle.properties file for bundled IntelliJ Platform modules.
        bundledModules(providers.gradleProperty("platformBundledModules").map { it.split(',') })
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
        testFramework(TestFrameworkType.JUnit5)
    }
}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            //sinceBuild = "252.25557"
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion")
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
    versionPrefix = ""
}

// Configure Gradle Kover Plugin - read more: https://kotlin.github.io/kotlinx-kover/gradle-plugin/#configuration-details
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

var gjfRequiredJvmArgs = listOf(
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
)

tasks {
    publishPlugin {
        dependsOn(patchChangelog)
    }

    runIde {
        jvmArgs.addAll(gjfRequiredJvmArgs)
        //jvmArgumentProviders += CommandLineArgumentProvider { gjfRequiredJvmArgs }
    }
}

//tasks {
//    withType<Test>().configureEach { jvmArgs(gjfRequiredJvmArgs) }
//}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgs.addAll(
                //jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                //}
                )
            }

            plugins {
                robotServerPlugin()
            }
        }
    }
}
