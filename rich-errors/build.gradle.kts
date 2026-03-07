plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.0-dev-5471" // specific dev version
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.4.0-dev-5471")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.4.0-dev-5471")
}

kotlin {
    jvmToolchain(17)
    
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xenable-union-types",
            "-Xrich-errors"
        )
    }
    
    sourceSets.all {
        languageSettings {
            progressiveMode = true
        }
    }
}

tasks.register<JavaExec>("run") {
    group = "application"
    mainClass.set("safemath.rich.UnionTestKt")
    classpath = sourceSets.main.get().runtimeClasspath
    
    // Redirect error output to standard output
    errorOutput = System.out
    
    // Or capture both to a file
    // standardOutput = file("build/run-output.txt").outputStream()
    // errorOutput = file("build/run-error.txt").outputStream()
    
    doFirst {
        println("Running with classpath:")
        classpath.files.forEach { println("  $it") }
    }
}