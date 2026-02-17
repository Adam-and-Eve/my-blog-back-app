plugins {
    id("java")
    id("war")
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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework:spring-test:7.0.3")

    implementation("org.springframework:spring-webmvc:7.0.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation("org.springframework.data:spring-data-jdbc:3.3.0")

    implementation("org.springframework:spring-jdbc:7.0.3")

    implementation("com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<War>("war") {
    archiveFileName.set("ROOT.war")
}