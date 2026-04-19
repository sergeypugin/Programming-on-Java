plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.postgresql:postgresql:42.7.3")
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}