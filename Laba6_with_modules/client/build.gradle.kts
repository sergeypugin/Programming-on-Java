plugins {
    java
}

dependencies {
    implementation(project(":common")) // Зависимость от общих классов
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "client.ClientMain"
    }
    // Собираем fat jar со всеми зависимостями
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}