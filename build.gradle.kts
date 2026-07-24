plugins {
    id("java")
    id("org.gretty") version "5.0.2"
}

group = "ru.yandex.practicum"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework:spring-test:7.0.7")

    implementation("org.springframework:spring-webmvc:7.0.7")
    implementation("tools.jackson.core:jackson-databind:3.1.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation("org.springframework:spring-jdbc:7.0.7")

    implementation("com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11")
}

tasks.test {
    useJUnitPlatform()
}

gretty {
    httpPort = 8080
    contextPath = "/"

    servletContainer = "jetty12"

    debugSuspend = true
}