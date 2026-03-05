// ---------- Custom Result ----------

sealed interface Result<out T, out E> {
    data class Ok<T>(val value: T) : Result<T, Nothing>
    data class Err<E>(val error: E) : Result<Nothing, E>
}

inline fun <T, E, R> Result<T, E>.match(
    onOk: (T) -> R,
    onErr: (E) -> R
): R = when (this) {
    is Result.Ok -> onOk(value)
    is Result.Err -> onErr(error)
}

// ---------- SafeMath ----------

class SafeMath {

    // ---------- NonZeroInt ----------

    sealed interface NonZeroInputError {
        data object WasZero : NonZeroInputError
        data object InvalidNumber : NonZeroInputError
    }

    @JvmInline
    value class NonZeroInt private constructor(private val n: Int) {
        companion object {
            fun from(i: Int): Result<NonZeroInt, NonZeroInputError> =
                if (i != 0) Result.Ok(NonZeroInt(i))
                else Result.Err(NonZeroInputError.WasZero)
        }

        fun unwrap(): Int = n
    }

    // ---------- Division Errors ----------

    //sealed interface DivisionError {
    //    data object DivideByZero : DivisionError
    //}

    object DivideByZero

    // ---------- Safe Division ----------

    fun safeDivide(a: Int, b: NonZeroInt): Result<Int, DivideByZero> {
        val denom = b.unwrap()
        return Result.Ok(a / denom)
    }

    fun safeDivide(a: Int, b: Int): Result<Int, DivideByZero> =
        NonZeroInt.from(b).match(
            onOk = { nz -> safeDivide(a, nz) },
            onErr = { Result.Err(DivideByZero) }
        )

    // ---------- Safe Modulo ----------

    fun safeModulo(a: Int, b: NonZeroInt): Result<Int, DivideByZero> {
        val denom = b.unwrap()
        return Result.Ok(a % denom)
    }

    fun safeModulo(a: Int, b: Int): Result<Int, DivideByZero> =
        NonZeroInt.from(b).match(
            onOk = { nz -> safeModulo(a, nz) },
            onErr = { Result.Err(DivideByZero) }
        )

    // ---------- Input Helpers ----------

    private val scanner = java.util.Scanner(System.`in`)

    fun readNonZero(): Result<NonZeroInt, NonZeroInputError> {
        return if (scanner.hasNextInt()) {
            val d = scanner.nextInt()
            NonZeroInt.from(d)
        } else {
            // consume the non-int token so the next call can progress
            if (scanner.hasNext()) scanner.next()
            Result.Err(NonZeroInputError.InvalidNumber)
        }
    }

    tailrec fun readNonZeroOrRetry(): NonZeroInt =
        when (val r = readNonZero()) {
            is Result.Ok -> r.value
            is Result.Err -> {
                println("Enter a non-zero number")
                readNonZeroOrRetry()
            }
        }
}

// ---------- Main ----------

fun main() {

    val math = SafeMath()

    println("Enter a non-zero number")
    val d: SafeMath.NonZeroInt = math.readNonZeroOrRetry()


    math.safeDivide(10, d).match(
        onOk = { println("SafeMath divide result: $it") },
        onErr = { println("SafeMath error: $it") }
    )
    math.safeModulo(10, d).match(
        onOk = { println("SafeMath modulo result: $it") },
        onErr = { println("SafeMath modulo error: $it") }
    )
    math.safeDivide(10, 0).match(
        onOk = { println("SafeMath divide result: $it") },
        onErr = { println("SafeMath error: $it") }
    )
    math.safeModulo(10, 0).match(
        onOk = { println("SafeMath modulo result: $it") },
        onErr = { println("SafeMath modulo error: $it") }
    )
}