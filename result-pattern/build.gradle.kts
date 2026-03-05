plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
