plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.0-dev-5318" // specific dev version
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.4.0-dev-5318")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.4.0-dev-5318")
}

kotlin {
    sourceSets.all {
        languageSettings {
            enableLanguageFeature("UnionTypes") // union types for rich error support
        }
    }
    
    jvmToolchain(17)
}