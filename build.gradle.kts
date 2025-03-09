plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta2"
    id("io.freefair.lombok") version "8.12.2.1"
}

group = "net.cc"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("redis.clients:jedis:5.2.0")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("net.kyori:adventure-api:4.20.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.3.0")
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