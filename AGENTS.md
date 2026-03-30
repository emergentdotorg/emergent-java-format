# AGENTS.md

This file provides guidance to AI agents when working with code in this repository.

## Project Overview

This is emergent-java-format, a fork of palantir-java-format (which itself is a fork of google-java-format). It's a Java source code formatter that uses 120-character line width and is optimized for lambda-heavy, modern Java code.

The project is transitioning from Gradle to Maven. The Maven build is the primary build system.

## Build Commands

```bash
# Build all modules
mvn clean install

# Build with native image (requires GraalVM 23+)
mvn clean install -Pnative

# Run tests
mvn test

# Run a single test class
mvn test -pl palantir-java-format -Dtest=FormatterIntegrationTest

# Run a single test method
mvn test -pl palantir-java-format -Dtest=FormatterIntegrationTest#format

# Recreate test output files (when updating expected formatter output)
mvn test -Drecreate=true

# Sort/format pom.xml files
mvn sortpom:sort
```

## Module Structure

- **palantir-java-format-spi**: Service Provider Interface - minimal API (`FormatterService`, `JavaFormatterOptions`, `Replacement`) for classpath isolation
- **palantir-java-format**: Core formatter implementation
- **palantir-java-format-jdk-bootstrap**: Bootstrap service for loading the formatter across different JDK versions
- **palantir-java-format-native**: Native image build using GraalVM (activated with `-Pnative` profile)

## Architecture

The formatter uses javac's parser to generate an AST, then walks it to emit formatting operations (`Op`s) using a Greg Nelson/Derek Oppen-style algorithm. The operations are converted to a structured `Doc` tree, which is then rendered with line-breaking decisions.

Key classes:
- `Formatter`: Main entry point for formatting Java source
- `JavaInputAstVisitor`: Walks the AST and emits formatting Ops (subclassed for Java 14+ and Java 21+ features)
- `DocBuilder`/`Doc`/`Level`: Document model for line-breaking decisions
- `State`: Tracks formatting state during document rendering
- `Main`: CLI entry point (`com.palantir.javaformat.java.Main`)

## JDK Compiler Internals

The formatter uses internal javac APIs. Compilation and tests require these JVM flags:
```
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

These are configured in the Maven Surefire and Compiler plugins.

## Testing

Integration tests use file-based test cases in `palantir-java-format/src/test/resources/com/palantir/javaformat/java/testdata/`:
- `.input` files contain unformatted Java code
- `.output` files contain expected formatted output

To regenerate expected outputs after intentional formatting changes:
```bash
mvn test -Drecreate=true
```

Enable debug mode for visual formatting exploration:
```bash
mvn test -DdebugOutput=true
# Then view in browser: cd debugger && yarn start
```

## Formatter Styles

Three styles are supported via `JavaFormatterOptions.Style`:
- `PALANTIR`: 120 chars, 2-space indent (default for this fork)
- `GOOGLE`: 100 chars, 1x indent
- `AOSP`: 100 chars, 2x indent

## CLI Usage

```bash
# Format files in place
java -jar palantir-java-format/target/emergent-java-format-*.jar -i src/**/*.java

# Check formatting (exit 1 if changes needed)
java -jar palantir-java-format/target/emergent-java-format-*.jar --set-exit-if-changed src/**/*.java

# Format from stdin
cat Foo.java | java -jar palantir-java-format/target/emergent-java-format-*.jar -
```
