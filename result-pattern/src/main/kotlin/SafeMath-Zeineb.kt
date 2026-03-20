package org.example.zeineb

object SafeMath {
    // ---------- Custom Result ----------
    sealed interface Result<out T, out E> {
        data class Ok<T>(val value: T) : Result<T, Nothing>
        data class Err<E>(val error: E) : Result<Nothing, E>
    }

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

    // ---------- Safe Division ----------
    fun safeDivide(a: Int, b: Int): Result<Int, DivisionError> =
        if (b == 0) Result.Err(DivisionError.DivisionByZero)
        else        Result.Ok(a / b)

    fun safeDivide(a: Long, b: Long): Result<Long, DivisionError> =
        if (b == 0L) Result.Err(DivisionError.DivisionByZero)
        else         Result.Ok(a / b)

    fun safeDivide(a: UInt, b: UInt): Result<UInt, DivisionError> =
        if (b == 0u) Result.Err(DivisionError.DivisionByZero)
        else         Result.Ok(a / b)

    fun safeDivide(a: ULong, b: ULong): Result<ULong, DivisionError> =
        if (b == 0UL) Result.Err(DivisionError.DivisionByZero)
        else          Result.Ok(a / b)

    // ---------- Safe Modulo ----------
    fun safeRem(a: Int, b: Int): Result<Int, ModuloError> =
        if (b == 0) Result.Err(ModuloError.ModuloByZero)
        else        Result.Ok(a % b)

    fun safeRem(a: Long, b: Long): Result<Long, ModuloError> =
        if (b == 0L) Result.Err(ModuloError.ModuloByZero)
        else         Result.Ok(a % b)

    fun safeRem(a: UInt, b: UInt): Result<UInt, ModuloError> =
        if (b == 0u) Result.Err(ModuloError.ModuloByZero)
        else         Result.Ok(a % b)

    fun safeRem(a: ULong, b: ULong): Result<ULong, ModuloError> =
        if (b == 0UL) Result.Err(ModuloError.ModuloByZero)
        else          Result.Ok(a % b)

    // ---------- Input Helpers ----------
    fun readNonZero(): Result<NonZeroInt, NonZeroInputError> =
        NonZeroInt.from(readLine()?.trim()?.toIntOrNull() ?: 0)

    fun readNonZeroOrRetry(): NonZeroInt {
        while (true) {
            val result = readNonZero()
            if (result is Result.Ok) {
                return result.value
            } else {
                println("Enter a non-zero number")
            }
        }
    }
}

// ---------- Extension Functions (Outside SafeMath object) ----------
inline fun <T, E, R> SafeMath.Result<T, E>.match(
    onOk: (T) -> R,
    onErr: (E) -> R
): R = when (this) {
    is SafeMath.Result.Ok  -> onOk(value)
    is SafeMath.Result.Err -> onErr(error)
}

inline fun <T, E, R> SafeMath.Result<T, E>.map(transform: (T) -> R): SafeMath.Result<R, E> =
    match(onOk = { SafeMath.Result.Ok(transform(it)) }, onErr = { SafeMath.Result.Err(it) })

inline fun <T, E, F> SafeMath.Result<T, E>.mapErr(transform: (E) -> F): SafeMath.Result<T, F> =
    match(onOk = { SafeMath.Result.Ok(it) }, onErr = { SafeMath.Result.Err(transform(it)) })

fun <T, E> SafeMath.Result<T, E>.getOrElse(default: T): T =
    match(onOk = { it }, onErr = { default })

// ---------- Operator Overloads (Outside SafeMath object) ----------
// The compiler rejects a plain Int as the right-hand operand.
operator fun Int.div(d: SafeMath.NonZeroInt): Int  = this / d.unwrap()
operator fun Int.rem(d: SafeMath.NonZeroInt): Int  = this % d.unwrap()
operator fun Long.div(d: SafeMath.NonZeroInt): Long = this / d.unwrap()
operator fun Long.rem(d: SafeMath.NonZeroInt): Long = this % d.unwrap()

// ---------- Main ----------
fun main() {
    // -- safeDivide (Int) --
    SafeMath.safeDivide(10, 2).match(
        onOk  = { println("10 / 2  = $it") },
        onErr = { println("10 / 2  → error: $it") }
    )
    SafeMath.safeDivide(10, 0).match(
        onOk  = { println("10 / 0  = $it") },
        onErr = { println("10 / 0  → caught: $it") }
    )

    // -- safeDivide (Long) --
    SafeMath.safeDivide(100L, 3L).match(
        onOk  = { println("100L / 3L = $it") },
        onErr = { println("100L / 3L → error: $it") }
    )
    SafeMath.safeDivide(100L, 0L).match(
        onOk  = { println("100L / 0L = $it") },
        onErr = { println("100L / 0L → caught: $it") }
    )

    // -- safeDivide (UInt) --
    SafeMath.safeDivide(50u, 7u).match(
        onOk  = { println("50u / 7u = $it") },
        onErr = { println("50u / 7u → error: $it") }
    )
    SafeMath.safeDivide(50u, 0u).match(
        onOk  = { println("50u / 0u = $it") },
        onErr = { println("50u / 0u → caught: $it") }
    )

    // -- safeDivide (ULong) --
    SafeMath.safeDivide(200UL, 9UL).match(
        onOk  = { println("200UL / 9UL = $it") },
        onErr = { println("200UL / 9UL → error: $it") }
    )

    // -- safeRem --
    SafeMath.safeRem(17, 5).match(
        onOk  = { println("17 % 5  = $it") },
        onErr = { println("17 % 5  → error: $it") }
    )
    SafeMath.safeRem(17, 0).match(
        onOk  = { println("17 % 0  = $it") },
        onErr = { println("17 % 0  → caught: $it") }
    )

        // -- NonZeroInt construction --
        val result1 = SafeMath.NonZeroInt.from(5)
        when (result1) {
            is SafeMath.Result.Ok<*> -> println("NonZeroInt.from(5)  → Ok(${result1.value})")
            is SafeMath.Result.Err<*> -> println("NonZeroInt.from(5)  → error: ${result1.error}")
        }
        val result2 = SafeMath.NonZeroInt.from(0)
        when (result2) {
            is SafeMath.Result.Ok<*> -> println("NonZeroInt.from(0)  → Ok(${result2.value})")
            is SafeMath.Result.Err<*> -> println("NonZeroInt.from(0)  → Err(${result2.error})")
        }

        // -- NonZeroInt operator overloads --
        val divisor = SafeMath.NonZeroInt.unsafe(3)
        println("100 / $divisor = ${100.div(divisor)}")
        println("100 % $divisor = ${100.rem(divisor)}")
        println("1000L / $divisor = ${1000L.div(divisor)}")
        println("1000L % $divisor = ${1000L.rem(divisor)}")

    // -- Result helpers --
    println("safeDivide(10,2).map{it*2}   = ${SafeMath.safeDivide(10, 2).map { it * 2 }.getOrElse(-1)}")
    println("safeDivide(10,0).getOrElse(-1) = ${SafeMath.safeDivide(10, 0).getOrElse(-1)}")

    // -- Interactive --
    println("\nEnter a non-zero number:")
    val d: SafeMath.NonZeroInt = SafeMath.readNonZeroOrRetry()
    println("10 / $d = ${10.div(d)}")
    println("10 % $d = ${10.rem(d)}")
}