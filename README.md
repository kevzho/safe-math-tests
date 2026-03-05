# safe-math-tests

Kotlin experiments for making `SafeMath` runtime failures explicit in types so invalid states are handled before execution paths fail.

## Goal

Build and compare two approaches:

1. `SafeMathResult` (result-pattern): traditional typed result wrappers.
2. `SafeMathRich` (rich-errors): richer typed error modeling using Kotlin 2.4 beta/dev features.

Design constraints across both:

- `NonZeroInt` for division/modulo denominators.
- No nullable return values for math operations.
- No thrown exceptions for expected math/input failures.
- Errors are values, returned in typed result channels.

## Repository Layout

- `result-pattern/`: stable Kotlin approach (`SafeMathResult`).
- `rich-errors/`: Kotlin 2.4 beta/dev approach (`SafeMathRich`).

## Approach 1: SafeMathResult (Result Pattern)

Use a sealed result type and force callers to handle success/error branches.

```kotlin
sealed interface Result<out T, out E> {
    data class Ok<T>(val value: T) : Result<T, Nothing>
    data class Err<E>(val error: E) : Result<Nothing, E>
}

@JvmInline
value class NonZeroInt private constructor(private val n: Int) {
    companion object {
        fun from(i: Int): Result<NonZeroInt, NonZeroInputError> =
            if (i != 0) Result.Ok(NonZeroInt(i))
            else Result.Err(NonZeroInputError.WasZero)
    }
}
```

This keeps divide-by-zero impossible once `NonZeroInt` is obtained.

## Approach 2: SafeMathRich (Rich Errors Library Style)

Use richer error-domain typing and Kotlin 2.4 beta/dev compiler features to model error flows more precisely than plain string errors.

Target use case:

- Library-first API (`SafeMathRich`) plus example code.
- Example usage first, then underlying classes/types.
- Borrow ergonomic patterns from Kotlin unit-test style examples.

## Kotlin 2.4 Beta/Dev Source (Recorded)

Beta/dev Kotlin artifacts are sourced from JetBrains Team packages:

- https://packages.jetbrains.team/maven/p/kt/dev/org/jetbrains/kotlin/kotlin-stdlib/

Current repository wiring:

- `settings.gradle.kts` includes `https://packages.jetbrains.team/maven/p/kt/dev` in plugin + dependency repositories.
- `rich-errors/build.gradle.kts` uses `org.jetbrains.kotlin.jvm` and stdlib/test at `2.4.0-dev-5318`.

## Development

From repo root:

```bash
cd safe-math-tests
./gradlew tasks
```

Build a specific approach:

```bash
./gradlew :result-pattern:build
./gradlew :rich-errors:build
```