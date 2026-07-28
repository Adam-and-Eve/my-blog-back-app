plugins {
    id("java")
    id("war")
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

    testImplementation("org.mockito:mockito-core:5.23.0")

    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")

    implementation("org.springframework:spring-webmvc:7.0.7")

    implementation("tools.jackson.core:jackson-databind:3.1.0")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation("org.springframework:spring-jdbc:7.0.7")

    implementation("com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11")

    testImplementation(platform("org.testcontainers:testcontainers-bom:1.21.4"))

    testImplementation("org.testcontainers:testcontainers")

    testImplementation("org.testcontainers:junit-jupiter")

    testImplementation("org.testcontainers:mssqlserver")

    testImplementation("org.slf4j:slf4j-simple:1.7.36")
}

tasks.test {
    useJUnitPlatform()

    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")

    systemProperty("mssql.encrypt", "false")

    systemProperty("mssql.trustServerCertificate", "true")
}

gretty {
    httpPort = 8080
    contextPath = "/"

    servletContainer = "jetty12"

    debugSuspend = true
}