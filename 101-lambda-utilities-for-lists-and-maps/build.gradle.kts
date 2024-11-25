plugins {
    java
    application
    id("org.danilopianini.gradle-java-qa") version "1.75.0"
}

tasks.javadoc {
    isFailOnError = false
}

repositories {
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
}

application {
    val main: String? by project
    // The following allows to run with: ./gradlew -Pmain=it.unibo.oop.MyMainClass run
    this.mainClass = main ?: "it.unibo.oop.lab.lambda.LambdaUtilities"
}

val test by tasks.getting(Test::class) {
    // Use junit platform for unit tests
    useJUnitPlatform()
    testLogging {
        events(*(org.gradle.api.tasks.testing.logging.TestLogEvent.values())) // events("passed", "skipped", "failed")
    }
    testLogging.showStandardStreams = true    
}
