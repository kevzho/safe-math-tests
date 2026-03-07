# Result Pattern-based Error Handling in Kotlin

A demonstration of functional error handling using the Result pattern in Kotlin, featuring type-safe division and modulo operations with non-zero validation.

## Overview

This project implements a `SafeMath` class that uses the Result pattern (similar to Rust's `Result<T, E>` or Haskell's `Either`) for elegant error handling without exceptions. It demonstrates:

- Type-safe error handling with sealed classes
- Value classes for runtime constraints
- Functional composition with `match`
- Recursive input validation

## Features

- **Result Type**: Custom `Result<T, E>` sealed interface with `Ok` and `Err` subtypes
- **NonZeroInt**: Value class that guarantees non-zero integers at compile time
- **Safe Division/Modulo**: Operations that return `Result` types instead of throwing exceptions
- **Comprehensive Testing**: Unit tests covering success cases, error cases, and edge cases

## Subfolder Structure

```
.
├── README.md
├── build
├── build.gradle.kts
├── simple-safe-tests.kt
└── src
    ├── main
    │   └── kotlin
    │       └── safe-math.kt # Micah's class implementation
    └── test
        └── kotlin
            └── SafeMathTest.kt 
```

## Running the Code

### Option 1: Using Gradle (Recommended)

```bash
# Build the project
../gradlew build

# Run the main program
../gradlew run

# Run tests
../gradlew test

# Run tests with console output
../gradlew test --info
```

You can also run specific tests:

```bash
# Run a specific test class
../gradlew test --tests "SafeMathTest"

# Run a specific test method
../gradlew test --tests "SafeMathTest.testSafeDivideWithValidInteger"

# View test report (after running tests)
# Open build/reports/tests/test/index.html in your browser
```

### Option 2: Compile and run the main program
```bash
kotlinc src/main/kotlin/safe-math.kt -include-runtime -d safemath.jar
java -jar safemath.jar
```

### Option 3: Run directly with Kotlin script mode
```bash
kotlinc -script src/main/kotlin/safe-math.kt
```

### Test Output:

Tests cover:

- Valid non-zero integer creation
- Zero input handling
- Division with valid divisors
- Division by zero error handling
- Modulo operations
- Edge cases (Int.MAX_VALUE, Int.MIN_VALUE)
- Match function behavior