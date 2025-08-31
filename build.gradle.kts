plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
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

dependencies {

    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
    // https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.springframework.integration:spring-integration-ip:6.0.0")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-mail
    implementation("org.springframework.boot:spring-boot-starter-mail:3.4.4")
    implementation("io.jsonwebtoken:jjwt:0.12.5")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // https://mvnrepository.com/artifact/com.influxdb/influxdb-client-java
    implementation("com.influxdb:influxdb-client-java:4.0.0")
    // https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
