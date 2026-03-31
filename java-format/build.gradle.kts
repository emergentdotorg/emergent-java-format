plugins {
    `java-library`
    id("me.champeau.mrjar") version "0.1.2"
    `maven-publish`
}

multiRelease {
    //addDependencyTo()
    targetVersions(11, 14, 21, 23)
}

tasks {
    // Set the JVM compatibility versions
    //withType<JavaCompile> {
    //    sourceCompatibility = "21"
    //    targetCompatibility = "21"
    //}
}

//sourceSets {
//    main {
//        java {
//            srcDirs("src/main/java", "build/generated/sources/annotationProcessor/java/jmh")
//        }
//    }
//}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

configurations {
    create("uber") {
        //artifacts {
        //    uberJar
        //}
        //formatter {
        //description = "The default implementation of palantir-java-format, used if the user doesn\'t specify an explicit classpath."
        //isCanBeConsumed = false
        //isCanBeResolved = true

        //canBeConsumed = false
        //canBeResolved = true
    }

    //getByName("java21CompileOnly") {
    //    artifacts {  }
    //    from (sourceSets.java14.output)
    //    //addAll(configurations.getByName("java"))
    //}
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("org.derive4j:derive4j:1.1.1")
    //annotationProcessor("org.immutables:value:${libs.versions.org.immutables.value}")
    annotationProcessor("org.immutables:value:2.12.1")

    compileOnly("org.immutables:value-annotations:2.12.1")

    api(libs.com.fasterxml.jackson.core.jackson.core)
    api(libs.com.fasterxml.jackson.core.jackson.databind)
    api(libs.com.fasterxml.jackson.datatype.jackson.datatype.guava)
    api(libs.com.fasterxml.jackson.datatype.jackson.datatype.jdk8)
    api(libs.com.fasterxml.jackson.module.jackson.module.parameter.names)
    api(libs.com.google.auto.service.auto.service)
    api(libs.com.google.auto.service.auto.service.annotations)
    api(libs.com.google.code.findbugs.jsr305)
    api(libs.com.google.errorprone.error.prone.annotations)
    api(libs.com.google.guava.guava)
    api(libs.derive4j.annotation)
    api(libs.org.functionaljava.functionaljava)
    //api(libs.org.immutables.value)
    //api(libs.org.immutables.annotations)
    api(libs.org.jspecify.jspecify)

    //compileOnly(libs.org.immutables.annotations)

    //annotationProcessor(libs.com.google.auto.service.auto.service)
    //annotationProcessor(libs.org.derive4j.derive4j.annotation)
    //annotationProcessor(libs.org.immutables.value)
    //implementation("org.immutables:value:2.12.1")
    //annotationProcessor("org.immutables:value:2.12.1")

    //java23Implementation()

    "java21CompileOnly"(sourceSets.getByName("java14").output)
    "java23CompileOnly"(sourceSets.getByName("java21").output)

    testImplementation(libs.com.google.truth.truth)
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.params)
    testImplementation(libs.org.slf4j.slf4j.simple)

}

group = "org.emergent.javaformat"
//version = "3.0.0-SNAPSHOT"
description = "java-format"
//java.sourceCompatibility = JavaVersion.VERSION_21

//configurations.create("java23CompileClasspath") {
//    extendsFrom(configurations.compileClasspath.get())
//}

var gjfRequiredJvmArgs = listOf(
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
)

tasks {
    //register("uberJar", Jar::class) {
    //    description = "Assembles jar archive with dependency classes bundled"
    //    archiveClassifier = "uber"
    //    from(sourceSets["main"].output)
    //    //destinationDirectory = layout.buildDirectory.dir("dist")
    //    //from(layout.buildDirectory.dir("toArchive"))
    //    from({
    //        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    //        configurations.runtimeClasspath.get().files.map { zipTree(it) }
    //        //configurations.getByName("formatter").filter { it.name.startsWith("java-format") }
    //        //.map { zipTree("impl/$it") }
    //    })
    //}

    withType<JavaCompile> {
        options.compilerArgs.addAll(gjfRequiredJvmArgs)
        //    options.compilerArgs.add("-Xlint:unchecked")
    }

    //names.filter { it.contains("compileJava") && !it.endsWith("compileJava") }
    //    .forEach { taskName ->
    //        getByName<JavaCompile>(taskName) {
    //            logger.info("Updating task $name")
    //            options.compilerArgs.addAll(gjfRequiredJvmArgs)
    //        }
    //    }

    //runIde {
    //    jvmArgumentProviders += CommandLineArgumentProvider { gjfRequiredJvmArgs }
    //}

jar {
    manifest {
        attributes(Pair("Class-Path", configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }))
    }
}

    //jar {
    //    into("META-INF/versions/9") {
    //        from (sourceSets.java9.output)
    //    }
    //
    //    manifest.attributes(Pair(
    //        "Multi-Release", "true"
    //    ))
    //}
}



val testsJar by tasks.registering(Jar::class) {
    archiveClassifier = "tests"
    from(sourceSets["test"].output)
}

val uberJar by tasks.registering(Jar::class) {
    description = "Assembles jar archive with dependency classes bundled"
    archiveClassifier = "uber"
    from(sourceSets["main"].output)
    //destinationDirectory = layout.buildDirectory.dir("dist")
    //from(layout.buildDirectory.dir("toArchive"))
    from({
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations.runtimeClasspath.get().files.map { zipTree(it) }
        //configurations.getByName("formatter").filter { it.name.startsWith("java-format") }
        //.map { zipTree("impl/$it") }
    })
}

artifacts {
    add("uber", uberJar)
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifacts {
            artifact(testsJar)
            add("uber", uberJar)
        }
        //artifact(uberJar)
        //artifact(testsJar)
    }
}

//tasks.getByName("java")

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    //options.compilerArgumentProviders.add(CommandLineArgumentProvider { gjfRequiredJvmArgs })
    //options.compilerArgs.add("-Xlint:unchecked")
    //options.compilerArgs.addAll(gjfRequiredJvmArgs)
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
    isFailOnError = false
}

tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}

tasks.getByName("assemble").dependsOn(tasks.getByName("uberJar"))
