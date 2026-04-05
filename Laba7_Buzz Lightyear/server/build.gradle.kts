plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(project(":common")) // Зависимость от общих классов
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "server.ServerMain"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(24)
}