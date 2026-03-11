// ---------- Custom Result ----------
sealed interface Result<out T, out E> {
    data class Ok<T>(val value: T) : Result<T, Nothing>
    data class Err<E>(val error: E) : Result<Nothing, E>
}

inline fun <T, E, R> Result<T, E>.match(
    onOk: (T) -> R,
    onErr: (E) -> R
): R = when (this) {
    is Result.Ok  -> onOk(value)
    is Result.Err -> onErr(error)
}

inline fun <T, E, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> =
    match(onOk = { Result.Ok(transform(it)) }, onErr = { Result.Err(it) })

inline fun <T, E, F> Result<T, E>.mapErr(transform: (E) -> F): Result<T, F> =
    match(onOk = { Result.Ok(it) }, onErr = { Result.Err(transform(it)) })

fun <T, E> Result<T, E>.getOrElse(default: T): T =
    match(onOk = { it }, onErr = { default })

// ---------- Error Types ----------
sealed interface DivisionError {
    data object DivisionByZero : DivisionError
}

sealed interface ModuloError {
    data object ModuloByZero : ModuloError
}

sealed interface NonZeroInputError {
    data object WasZero : NonZeroInputError
}

// ---------- NonZeroInt ----------
// @JvmInline erases the wrapper at runtime — identical bytecode to a plain Int,
// zero heap allocation or boxing overhead.
// Private constructor means the only construction path is from(), which rejects zero.
@JvmInline
value class NonZeroInt private constructor(private val n: Int) {
    companion object {
        fun from(i: Int): Result<NonZeroInt, NonZeroInputError> =
            if (i != 0) Result.Ok(NonZeroInt(i))
            else        Result.Err(NonZeroInputError.WasZero)

        fun unsafe(i: Int): NonZeroInt {
            require(i != 0) { "NonZeroInt.unsafe called with 0" }
            return NonZeroInt(i)
        }
    }
    fun unwrap(): Int = n
    override fun toString(): String = "NonZeroInt($n)"
}

// ---------- Operator Overloads ----------
// The compiler rejects a plain Int as the right-hand operand.
operator fun Int.div(d: NonZeroInt): Int  = this / d.unwrap()
operator fun Int.rem(d: NonZeroInt): Int  = this % d.unwrap()
operator fun Long.div(d: NonZeroInt): Long = this / d.unwrap()
operator fun Long.rem(d: NonZeroInt): Long = this % d.unwrap()

// ---------- Safe Division ----------
fun safeDivide(a: Int, b: Int): Result<Int, DivisionError> =
    if (b == 0) Result.Err(DivisionError.DivisionByZero)
    else        Result.Ok(a / b)

fun safeDivide(a: Double, b: Double): Result<Double, DivisionError> =
    if (b == 0.0) Result.Err(DivisionError.DivisionByZero)
    else          Result.Ok(a / b)

fun safeDivide(a: Long, b: Long): Result<Long, DivisionError> =
    if (b == 0L) Result.Err(DivisionError.DivisionByZero)
    else         Result.Ok(a / b)

// ---------- Safe Modulo ----------
fun safeRem(a: Int, b: Int): Result<Int, ModuloError> =
    if (b == 0) Result.Err(ModuloError.ModuloByZero)
    else        Result.Ok(a % b)

fun safeRem(a: Long, b: Long): Result<Long, ModuloError> =
    if (b == 0L) Result.Err(ModuloError.ModuloByZero)
    else         Result.Ok(a % b)

// ---------- Input Helpers ----------
fun readNonZero(): Result<NonZeroInt, NonZeroInputError> =
    NonZeroInt.from(readLine()?.trim()?.toIntOrNull() ?: 0)

fun readNonZeroOrRetry(): NonZeroInt =
    readNonZero().match(
        onOk  = { it },
        onErr = { println("Enter a non-zero number"); readNonZeroOrRetry() }
    )

// ---------- Main ----------
fun main() {
    // -- safeDivide (Int) --
    safeDivide(10, 2).match(
        onOk  = { println("10 / 2  = $it") },
        onErr = { println("10 / 2  → error: $it") }
    )
    safeDivide(10, 0).match(
        onOk  = { println("10 / 0  = $it") },
        onErr = { println("10 / 0  → caught: $it") }
    )

    // -- safeDivide (Double) --
    safeDivide(7.5, 2.5).match(
        onOk  = { println("7.5 / 2.5 = $it") },
        onErr = { println("7.5 / 2.5 → error: $it") }
    )
    safeDivide(7.5, 0.0).match(
        onOk  = { println("7.5 / 0.0 = $it") },
        onErr = { println("7.5 / 0.0 → caught: $it") }
    )

    // -- safeRem --
    safeRem(17, 5).match(
        onOk  = { println("17 % 5  = $it") },
        onErr = { println("17 % 5  → error: $it") }
    )
    safeRem(17, 0).match(
        onOk  = { println("17 % 0  = $it") },
        onErr = { println("17 % 0  → caught: $it") }
    )

    // -- NonZeroInt construction --
    NonZeroInt.from(5).match(
        onOk  = { println("NonZeroInt.from(5)  → Ok($it)") },
        onErr = { println("NonZeroInt.from(5)  → error: $it") }
    )
    NonZeroInt.from(0).match(
        onOk  = { println("NonZeroInt.from(0)  → Ok($it)") },
        onErr = { println("NonZeroInt.from(0)  → Err($it)") }
    )

    // -- NonZeroInt operator overloads --
    val divisor = NonZeroInt.unsafe(3)
    println("100 / $divisor = ${100 / divisor}")
    println("100 % $divisor = ${100 % divisor}")

    // -- Result helpers --
    println("safeDivide(10,2).map{it*2}   = ${safeDivide(10, 2).map { it * 2 }.getOrElse(-1)}")
    println("safeDivide(10,0).getOrElse(-1) = ${safeDivide(10, 0).getOrElse(-1)}")

    // -- Interactive --
    println("\nEnter a non-zero number:")
    val d: NonZeroInt = readNonZeroOrRetry()
    println("10 / $d = ${10 / d}")
    println("10 % $d = ${10 % d}")
}