plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta2"
    id("io.freefair.lombok") version "8.12.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

group = "com.spektrsoyuz"
version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.bencodez.com/repository/maven-public/")
}

dependencies {
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.spongepowered:configurate-hocon:4.2.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}