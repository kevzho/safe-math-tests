plugins {
    // nothing here
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

subprojects {
    // Common configuration for all subprojects if needed
}