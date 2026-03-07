# safe-math-tests

Kotlin experiments for making `SafeMath` runtime failures explicit in types so invalid states are handled before execution paths fail.

## Goal

Build and compare two approaches:

1. **`SafeMathResult` (result-pattern)**: traditional typed result wrappers.
2. **`SafeMathRich` (rich-errors)** (⚠️ **currently non-functional**): intended to explore richer typed error modeling using Kotlin 2.4 beta/dev features.

Design constraints across both:

- `NonZeroInt` for division/modulo denominators.
- No nullable return values for math operations.
- No thrown exceptions for expected math/input failures.
- Errors are values, returned in typed result channels.

## Repository Layout

- `result-pattern/`: **Working** stable Kotlin approach (`SafeMathResult`) with comprehensive tests.
- `rich-errors/`: ⚠️ **BROKEN/EXPERIMENTAL** - Kotlin 2.4 beta/dev approach (`SafeMathRich`). Currently non-functional due to compiler instability.

## Approach 1: SafeMathResult (Working)

A stable implementation using a sealed result type that forces callers to handle success/error branches.

### Key Features

- **Result Type**: Custom `Result<T, E>` sealed interface with `Ok` and `Err` subtypes
- **NonZeroInt**: Value class guaranteeing non-zero integers at compile time
- **No Nullable Returns**: Math operations never return null
- **No Exceptions**: Expected failures returned as values
- **Exhaustive Handling**: `match` function forces handling both cases
- **Comprehensive Tests**: Full test coverage for all scenarios

### Code Example

```kotlin
val math = SafeMath()
val result = math.safeDivide(10, 2)

result.match(
    onOk = { quotient -> println("Result: $quotient") },
    onErr = { error -> println("Error: $error") }
)
```

### Test Coverage

- NonZeroInt creation (positive, negative, zero)
- Division operations (valid, zero, negative)
- Modulo operations
- Match function behavior
- Edge cases (Int.MAX/MIN_VALUE)
- Division property verification

### Running

```bash
# Build and test
./gradlew :result-pattern:build
./gradlew :result-pattern:test

# Run
cd result-pattern
kotlinc src/main/kotlin/safe-math.kt -include-runtime -d safemath.jar
java -jar safemath.jar
```

## Approach 2: SafeMathRich ⚠️ (Broken - Do Not Use)

**Status:** Currently non-functional. This approach attempted to use Kotlin 2.4 beta/dev features for richer error-domain typing, but the compiler is too unstable for practical use.

### Issues Encountered

- Kotlin 2.4.0-dev builds are highly unstable
- Compiler crashes during incremental compilation
- Incompatible with standard tooling
- No stable API guarantees

### Historical Context

The intended goal was to explore:

- Library-first API design
- Richer error-domain typing beyond simple sealed hierarchies
- Ergonomics inspired by Kotlin unit-test style examples

### Current Status

This module is preserved only for reference. Do not attempt to build or run it — it will fail with compiler errors.

## Development Status Summary

| Module | Status | Kotlin Version | Test Coverage |
|---|---|---|---|
| `result-pattern/` | Working | 2.2.21 (stable) | Full |
| `rich-errors/` | Broken | 2.4.0-dev (unstable) | None |

## Running Working Code

From repo root:

```bash
# Build the working module only
./gradlew :result-pattern:build

# Run tests for working module
./gradlew :result-pattern:test

# View test report
open result-pattern/build/reports/tests/test/index.html
```

## Requirements for Working Module

- Kotlin 2.2.21 or higher
- Java 17 or higher
- Gradle