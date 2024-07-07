plugins {
    java
    pmd
    jacoco

    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}

group = "com.wcc.cms"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")


    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule { limit { minimum = BigDecimal.valueOf(0.6) } }
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

logging.captureStandardOutput(LogLevel.INFO)