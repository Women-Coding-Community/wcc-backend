import java.time.Duration

plugins {
    java
    pmd
    jacoco

    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
    id("org.openapi.generator") version "7.4.0"
    id("org.sonarqube") version "6.3.1.5724"
}

group = "com.wcc.cms"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

sourceSets {
    val testInt by creating {
        java.srcDir("src/testInt/java")
        resources.srcDir("src/testInt/resources")
        compileClasspath += sourceSets.getByName("main").output +
                sourceSets.getByName("test").output +
                configurations.getByName("testRuntimeClasspath")

        runtimeClasspath += output + compileClasspath
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val testContainer = "1.21.4"

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.java-websocket:Java-WebSocket:1.5.7")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("com.h2database:h2:2.2.224")

    // Google Drive API
    implementation("com.google.api-client:google-api-client:2.8.1")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation("org.testcontainers:testcontainers:$testContainer")
    testImplementation("org.testcontainers:junit-jupiter:$testContainer")
    testImplementation("org.testcontainers:postgresql:$testContainer")
    testImplementation("org.apiguardian:apiguardian-api:1.1.2")
    testImplementation("com.icegreen:greenmail-spring:2.0.1")
    testImplementation("com.icegreen:greenmail-junit5:2.0.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("-Xmx2048m")
    timeout.set(Duration.ofMinutes(2))
}

tasks {
    // Configure the standard jacocoTestReport task for unit tests
    jacocoTestReport {
        dependsOn(test) // tests are required to run before generating the report
        reports {
            xml.required.set(true)  // Enable XML report for SonarQube
            html.required.set(true) // Keep HTML for local viewing
            csv.required.set(false) // Disable CSV to reduce clutter
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/integration"))
        }
    }

    val testIntegration by creating(Test::class) {
        description = "Runs integration tests."
        group = "verification"
        testClassesDirs = sourceSets["testInt"].output.classesDirs
        classpath = sourceSets["testInt"].runtimeClasspath
        useJUnitPlatform()
        shouldRunAfter("test")
        timeout.set(Duration.ofMinutes(2))

        jacoco {
            isEnabled = true
        }
    }

    val jacocoIntegrationReport by creating(JacocoReport::class) {
        description = "Generates code coverage report for integration tests."
        group = "verification"
        dependsOn(testIntegration)

        executionData(testIntegration)
        sourceSets(sourceSets.main.get())

        reports {
            xml.required.set(true)
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/integration"))
        }
    }

    jacocoTestCoverageVerification {
        executionData(
            fileTree(layout.buildDirectory.asFile.get()).include(
                "jacoco/test.exec",
                "jacoco/testIntegration.exec"
            )
        )
        violationRules {
            rule { limit { minimum = BigDecimal.valueOf(0.7) } }
        }
    }

    check {
        dependsOn("pmdAll", jacocoTestCoverageVerification)
    }
}

tasks.withType<Pmd> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    doFirst {
        println("Running PMD...")
    }
    doLast {
        println("PMD completed.")
    }
}

pmd {
    toolVersion = "7.3.0"
    isConsoleOutput = true
    ruleSets = listOf()
    ruleSetFiles = files("config/pmd/custom-ruleset.xml")
}

tasks.named<Pmd>("pmdMain") {
    exclude("**/FileUtil.java")
    exclude("**/PlatformApplication.java")
}

tasks.named<Pmd>("pmdTest") {
    ruleSetFiles = files("config/pmd/custom-ruleset-test.xml")
}

tasks.named<Pmd>("pmdTestInt") {
    ruleSetFiles = files("config/pmd/custom-ruleset-test.xml")
}

tasks.register("sonarQubeAnalysis") {
    group = "Code Quality"
    description = "Runs sonar analysis on the project."
    dependsOn("test")
    finalizedBy("sonar")
}

if (project.hasProperty("localProfile")) {
    apply(plugin = "org.sonarqube")
    sonarqube {
        properties {
            property("sonar.projectKey", "wcc-backend")
            property("sonar.projectName", "wcc-backend")
            property("sonar.host.url", "http://localhost:9000")
            property("sonar.token", "PLACE_YOUR_TOKEN_HERE")
            property(
                "sonar.exclusions",
                "**/src/main/resources/**,build.gradle.kts,gradle.properties,settings.gradle.kts"
            )
            property("sonar.sources", "src/main/java")
            property("sonar.tests", "src/test/java,src/testInt/java")
            property("sonar.java.binaries", "build/classes/java/main")
            property(
                "sonar.java.test.binaries",
                "build/classes/java/test,build/classes/java/testInt"
            )
            property("sonar.junit.reportPaths", "build/test-results/test")
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "build/reports/jacoco/test/jacocoTestReport.xml"
            )
            property("sonar.qualitygate.wait", "true")
        }
    }
} else {
    apply(plugin = "org.sonarqube")
    sonarqube {
        properties {
            property("sonar.projectKey", "Women-Coding-Community_wcc-backend")
            property("sonar.organization", "women-coding-community")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.token", System.getenv("SONAR_TOKEN") ?: "your-token")
            property(
                "sonar.exclusions",
                "**/src/main/resources/**,build.gradle.kts,gradle.properties,settings.gradle.kts"
            )
            property("sonar.sources", "src/main/java")
            property("sonar.tests", "src/test/java,src/testInt/java")
            property("sonar.java.binaries", "build/classes/java/main")
            property(
                "sonar.java.test.binaries",
                "build/classes/java/test,build/classes/java/testInt"
            )
            property("sonar.junit.reportPaths", "build/test-results/test")
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "build/reports/jacoco/test/jacocoTestReport.xml,build/reports/jacoco/jacocoIntegrationReport/jacocoIntegrationReport.xml"
            )
            property("sonar.qualitygate.wait", "true")
        }
    }
}

tasks.register(
    "postmanGenerate",
    org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class.java
) {
    group = "openapi"
    description = "Generate Postman collection from OpenAPI spec"

    generatorName.set("postman-collection")
    inputSpec.set("$rootDir/postman-collection/openapi.yaml")
    outputDir.set("$rootDir/postman-collection/generated-collection")

    validateSpec.set(false)
}

tasks.named<ProcessResources>("processTestIntResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

logging.captureStandardOutput(LogLevel.INFO)

tasks.register("pmdAll") {
    group = "verification"
    description = "Runs all PMD checks (main, test, testInt)."
    dependsOn("pmdMain", "pmdTest", "pmdTestInt")
}

