pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // jebrains team repository to access dev builds
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/kt/dev")
            content {
                includeGroup("org.jetbrains.kotlin")
                includeGroup("org.jetbrains.kotlin.jvm")
                includeGroup("org.jetbrains.kotlin.plugins")
            }
        }
        // keeping old as fallback
        // maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // add for runtime dependencies
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/kt/dev")
            content {
                includeGroup("org.jetbrains.kotlin")
            }
        }
    }
}

rootProject.name = "safe-math-tests"

include("result-pattern") // stable kotlin 2.0.2
include("rich-errors") // dev kotlin 2.4.x