import java.time.Duration

plugins {
    java
    pmd
    jacoco

    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
    id("org.sonarqube") version "5.0.0.4638"
}

group = "com.wcc.cms"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

sourceSets {
    create("testInt") {
        java.srcDir("src/testInt/java")
        resources.srcDir("src/testInt/resources")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val testContainer = "1.21.0"

dependencies {

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.java-websocket:Java-WebSocket:1.5.7")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation("org.testcontainers:testcontainers:${testContainer}")
    testImplementation("org.testcontainers:junit-jupiter:$testContainer")

    testImplementation("org.testcontainers:postgresql:${testContainer}")
    testImplementation("org.apiguardian:apiguardian-api:1.1.2")

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
    jacocoTestCoverageVerification {
        violationRules {
            rule { limit { minimum = BigDecimal.valueOf(0.7) } }
        }
    }
    check {
        dependsOn(jacocoTestCoverageVerification)
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
    description = "Runs sonarQube analysis on the project."
    dependsOn("test")
    finalizedBy("sonarqube")
}

if (project.hasProperty("localProfile")) {
    apply(plugin = "org.sonarqube")
    sonarqube {
        properties {
            property("sonar.projectKey", "wcc-backend")
            property("sonar.projectName", "wcc-backend")
            property("sonar.host.url", "http://localhost:9000")
            property("sonar.token", "PLACE_YOUR_TOKEN_HERE")
        }
    }
}

tasks.register<Test>("testIntegration") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["testInt"].output.classesDirs
    classpath = sourceSets["testInt"].runtimeClasspath
    useJUnitPlatform()
    shouldRunAfter("test")
}

logging.captureStandardOutput(LogLevel.INFO)