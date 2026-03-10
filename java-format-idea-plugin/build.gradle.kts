import com.github.javaparser.printer.concretesyntaxmodel.CsmElement.token
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    //id("java")
    // https://github.com/JetBrains/intellij-platform-gradle-plugin/releases
    id("org.jetbrains.intellij.platform") version "2.12.0"
    // See https://plugins.jetbrains.com/docs/intellij/using-kotlin.html#bundled-stdlib-versions
    // This version of Kotlin will crash if your Gradle daemon is running under Java 25 (even if that
    // isn't the JDK you're using to compile). So make sure to update JAVA_HOME and then
    // `./gradlew --stop`
    kotlin("jvm") version "2.2.20"
}

group = "org.emergent.javaformat"
version = "3.0.0-SNAPSHOT"

val javaFormatVersion = version
val pluginPatchVersion = "0"

repositories {
    mavenCentral()
    mavenLocal()
    intellijPlatform { defaultRepositories() }
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin { jvmToolchain(21) }

intellijPlatform {
    pluginConfiguration {
        name = "emergent-java-format"
        version = "${javaFormatVersion}.${pluginPatchVersion}"
        ideaVersion {
            sinceBuild = "253"
            untilBuild = provider { null }
        }
    }

    publishing {
        val jetbrainsPluginRepoToken: String by project
        token.set(jetbrainsPluginRepoToken)
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

tasks { runIde { jvmArgumentProviders += CommandLineArgumentProvider { gjfRequiredJvmArgs } } }

tasks { withType<Test>().configureEach { jvmArgs(gjfRequiredJvmArgs) } }

dependencies {
    intellijPlatform {
        //intellijIdeaCommunity("2024.3")
        intellijIdea("2025.3.3")
        bundledPlugin("com.intellij.java")
        testFramework(TestFrameworkType.Plugin.Java)
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.JUnit5)
    }
    //implementation("com.google.googlejavaformat:google-java-format:${googleJavaFormatVersion}")
    implementation("org.emergent.javaformat:java-format:${javaFormatVersion}")
    //implementation("org.emergent.javaformat:emergent-java-format-jdk-bootstrap:${javaFormatVersion}")
    //implementation("com.palantir.sls.versions:sls-versions:1.11.0")
    implementation("com.github.zafarkhaja:java-semver:0.10.2")
    implementation("org.jspecify:jspecify:1.0.0")
    // https://mvnrepository.com/artifact/junit/junit
    testImplementation("junit:junit:4.13.2")
    // https://mvnrepository.com/artifact/com.google.truth/truth
    testImplementation("com.google.truth:truth:1.4.5")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter-migrationsupport:5.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.2")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
