plugins {
    java
    war
    id("com.diffplug.spotless") version "8.0.0"
    id("com.github.spotbugs") version "6.2.5"
}

spotbugs {
    toolVersion.set("4.8.3") // SpotBugs core version
    effort.set(com.github.spotbugs.snom.Effort.MAX) // MAX, DEFAULT, MIN
    reportLevel.set(com.github.spotbugs.snom.Confidence.LOW) // LOW, MEDIUM, HIGH
}

spotless {
    java {
        eclipse().configFile("config/eclipse-java-formatter.xml")
        removeUnusedImports()
    }
    kotlinGradle {
        ktlint().editorConfigOverride(
            mapOf(
                "indent_size" to 4,
                "continuation_indent_size" to 4,
            ),
        )
    }
}

group = "se.ifmo"
version = "0.0.1-SNAPSHOT"
description = "API для простого веб-приложения на Spring MVC + Eclipse Link"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring MVC & Core
    implementation("org.springframework:spring-webmvc:6.2.11")
    implementation("org.springframework:spring-context:6.2.11")
    implementation("org.springframework:spring-tx:6.2.11")
    implementation("org.springframework:spring-orm:6.2.11")
    implementation("org.springframework.security:spring-security-core:6.5.5")
    implementation("org.springframework.security:spring-security-web:6.5.5")
    implementation("org.springframework:spring-websocket:6.2.11")
    implementation("org.springframework:spring-messaging:6.2.11")
    implementation("org.springframework.security:spring-security-config:6.5.5")
    implementation("org.springframework.data:spring-data-jpa:3.5.4")

    // JSON for @RestController responses
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.0") // <-- Java time
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.20.0") // nice-to-have for records

    // JPA + EclipseLink
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.8") // EclipseLink
    implementation("org.hibernate.validator:hibernate-validator:9.0.1.Final")

    // JDBC + HikariCP (or your pool)
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("org.postgresql:postgresql:42.7.8")

    // Servlet API provided by Tomcat
    providedCompile("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    // Logs
    implementation("org.slf4j:slf4j-api:2.0.13")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.19")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
