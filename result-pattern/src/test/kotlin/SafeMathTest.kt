import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class SafeMathTest {
    
    private lateinit var math: SafeMath
    
    @BeforeTest
    fun setup() {
        math = SafeMath()
    }
    
    // ---------- NonZeroInt Tests ----------
    
    @Test
    fun testNonZeroIntFromPositiveNumber() {
        val result = SafeMath.NonZeroInt.from(5)
        
        assertTrue(result is Result.Ok)
        assertEquals(5, (result as Result.Ok).value.unwrap())
    }
    
    @Test
    fun testNonZeroIntFromNegativeNumber() {
        val result = SafeMath.NonZeroInt.from(-3)
        
        assertTrue(result is Result.Ok)
        assertEquals(-3, (result as Result.Ok).value.unwrap())
    }
    
    @Test
    fun testNonZeroIntFromZero() {
        val result = SafeMath.NonZeroInt.from(0)
        
        assertTrue(result is Result.Err)
        assertEquals(SafeMath.NonZeroInputError.WasZero, (result as Result.Err).error)
    }
    
    // ---------- Safe Division Tests ----------
    
    @Test
    fun testSafeDivideWithNonZeroInt() {
        // Arrange
        val nonZero = (SafeMath.NonZeroInt.from(2) as Result.Ok).value
        
        // Act
        val result = math.safeDivide(10, nonZero)
        
        // Assert
        assertTrue(result is Result.Ok)
        assertEquals(5, (result as Result.Ok).value)
    }
    
    @Test
    fun testSafeDivideWithNegativeDivisor() {
        val nonZero = (SafeMath.NonZeroInt.from(-2) as Result.Ok).value
        val result = math.safeDivide(10, nonZero)
        
        assertTrue(result is Result.Ok)
        assertEquals(-5, (result as Result.Ok).value)
    }
    
    @Test
    fun testSafeDivideWithZeroInteger() {
        val result = math.safeDivide(10, 0)
        
        assertTrue(result is Result.Err)
        assertEquals(SafeMath.DivideByZero, (result as Result.Err).error)
    }
    
    @Test
    fun testSafeDivideWithValidInteger() {
        val result = math.safeDivide(10, 2)
        
        assertTrue(result is Result.Ok)
        assertEquals(5, (result as Result.Ok).value)
    }
    
    // ---------- Safe Modulo Tests ----------
    
    @Test
    fun testSafeModuloWithNonZeroInt() {
        val nonZero = (SafeMath.NonZeroInt.from(3) as Result.Ok).value
        val result = math.safeModulo(10, nonZero)
        
        assertTrue(result is Result.Ok)
        assertEquals(1, (result as Result.Ok).value)
    }
    
    @Test
    fun testSafeModuloWithZeroInteger() {
        val result = math.safeModulo(10, 0)
        
        assertTrue(result is Result.Err)
        assertEquals(SafeMath.DivideByZero, (result as Result.Err).error)
    }
    
    // ---------- Match Function Tests ----------
    
    @Test
    fun testMatchWithOkResult() {
        val ok: Result<Int, String> = Result.Ok(42)
        
        val result = ok.match(
            onOk = { it * 2 },
            onErr = { -1 }
        )
        
        assertEquals(84, result)
    }
    
    @Test
    fun testMatchWithErrResult() {
        val err: Result<Int, String> = Result.Err("error")
        
        val result = err.match(
            onOk = { it * 2 },
            onErr = { -1 }
        )
        
        assertEquals(-1, result)
    }
    
    // ---------- Edge Case Tests ----------
    
    @Test
    fun testSafeDivideWithLargeNumbers() {
        val result = math.safeDivide(Int.MAX_VALUE, 1)
        assertTrue(result is Result.Ok)
        assertEquals(Int.MAX_VALUE, (result as Result.Ok).value)
    }
    
    @Test
    fun testSafeDivideWithMinimumInt() {
        val result = math.safeDivide(Int.MIN_VALUE, 1)
        assertTrue(result is Result.Ok)
        assertEquals(Int.MIN_VALUE, (result as Result.Ok).value)
    }
    
    @Test
    fun testSafeDivideWithNegativeDividend() {
        val result = math.safeDivide(-10, 2)
        assertTrue(result is Result.Ok)
        assertEquals(-5, (result as Result.Ok).value)
    }
    
    // ---------- Property-Based Test ----------
    
    @Test
    fun testDivisionProperty() {
        val a = 10
        val b = 2
        
        val result = math.safeDivide(a, b)
        
        result.match(
            onOk = { quotient -> 
                assertEquals(a, quotient * b) 
            },
            onErr = { fail("Should not error for valid division") }
        )
    }
}


// ==========================================================================
// Either
//
// A second way to model two outcomes — neutral where Result is opinionated.
// Result signals intent:  Ok = success,   Err = failure
// Either is just shapes:  Right = success (by convention), Left = failure
//
// Same constraints as Result:
//   - No nulls
//   - No thrown exceptions
//   - Errors are values
//
// Result<T, E>           Either<L, R>
// ──────────────────     ──────────────────
// Result.Ok(value)   →   Either.Right(value)
// Result.Err(error)  →   Either.Left(error)
// .match(onOk, onErr)→   .fold(onLeft, onRight)
// ==========================================================================

sealed interface Either<out L, out R> {
    data class Left<L>(val value: L)  : Either<L, Nothing>
    data class Right<R>(val value: R) : Either<Nothing, R>
}

inline fun <L, R, T> Either<L, R>.fold(
    onLeft:  (L) -> T,
    onRight: (R) -> T
): T = when (this) {
    is Either.Left  -> onLeft(value)
    is Either.Right -> onRight(value)
}

class SafeMathEither {

    sealed interface NonZeroInputError {
        data object WasZero       : NonZeroInputError
        data object InvalidNumber : NonZeroInputError
    }

    object DivideByZero

    @JvmInline
    value class NonZeroInt private constructor(private val n: Int) {
        companion object {
            fun from(i: Int): Either<NonZeroInputError, NonZeroInt> =
                if (i != 0) Either.Right(NonZeroInt(i))
                else        Either.Left(NonZeroInputError.WasZero)
        }
        fun unwrap(): Int = n
    }

    fun safeDivide(a: Int, b: NonZeroInt): Either<DivideByZero, Int> =
        Either.Right(a / b.unwrap())

    fun safeDivide(a: Int, b: Int): Either<DivideByZero, Int> =
        NonZeroInt.from(b).fold(
            onLeft  = { Either.Left(DivideByZero) },
            onRight = { nz -> safeDivide(a, nz) }
        )

    fun safeModulo(a: Int, b: NonZeroInt): Either<DivideByZero, Int> =
        Either.Right(a % b.unwrap())

    fun safeModulo(a: Int, b: Int): Either<DivideByZero, Int> =
        NonZeroInt.from(b).fold(
            onLeft  = { Either.Left(DivideByZero) },
            onRight = { nz -> safeModulo(a, nz) }
        )
}

class SafeMathEitherTest {

    private val math = SafeMathEither()

    // ---------- NonZeroInt Tests ----------

    @Test
    fun testEitherNonZeroIntFromPositiveNumber() {
        val result = SafeMathEither.NonZeroInt.from(5)

        assertTrue(result is Either.Right)
        assertEquals(5, (result as Either.Right).value.unwrap())
    }

    @Test
    fun testEitherNonZeroIntFromNegativeNumber() {
        val result = SafeMathEither.NonZeroInt.from(-3)

        assertTrue(result is Either.Right)
        assertEquals(-3, (result as Either.Right).value.unwrap())
    }

    @Test
    fun testEitherNonZeroIntFromZero() {
        val result = SafeMathEither.NonZeroInt.from(0)

        assertTrue(result is Either.Left)
        assertEquals(
            SafeMathEither.NonZeroInputError.WasZero,
            (result as Either.Left).value
        )
    }

    // ---------- Safe Division Tests ----------

    @Test
    fun testEitherSafeDivideWithNonZeroInt() {
        val nonZero = (SafeMathEither.NonZeroInt.from(2) as Either.Right).value
        val result  = math.safeDivide(10, nonZero)

        assertTrue(result is Either.Right)
        assertEquals(5, (result as Either.Right).value)
    }

    @Test
    fun testEitherSafeDivideWithNegativeDivisor() {
        val nonZero = (SafeMathEither.NonZeroInt.from(-2) as Either.Right).value
        val result  = math.safeDivide(10, nonZero)

        assertTrue(result is Either.Right)
        assertEquals(-5, (result as Either.Right).value)
    }

    @Test
    fun testEitherSafeDivideWithZeroInteger() {
        val result = math.safeDivide(10, 0)

        assertTrue(result is Either.Left)
        assertEquals(SafeMathEither.DivideByZero, (result as Either.Left).value)
    }

    @Test
    fun testEitherSafeDivideWithValidInteger() {
        val result = math.safeDivide(10, 2)

        assertTrue(result is Either.Right)
        assertEquals(5, (result as Either.Right).value)
    }

    // ---------- Safe Modulo Tests ----------

    @Test
    fun testEitherSafeModuloWithNonZeroInt() {
        val nonZero = (SafeMathEither.NonZeroInt.from(3) as Either.Right).value
        val result  = math.safeModulo(10, nonZero)

        assertTrue(result is Either.Right)
        assertEquals(1, (result as Either.Right).value)
    }

    @Test
    fun testEitherSafeModuloWithZeroInteger() {
        val result = math.safeModulo(10, 0)

        assertTrue(result is Either.Left)
        assertEquals(SafeMathEither.DivideByZero, (result as Either.Left).value)
    }

    // ---------- Fold Tests ----------

    @Test
    fun testFoldWithRight() {
        val right: Either<String, Int> = Either.Right(42)

        val result = right.fold(
            onLeft  = { -1 },
            onRight = { it * 2 }
        )

        assertEquals(84, result)
    }

    @Test
    fun testFoldWithLeft() {
        val left: Either<String, Int> = Either.Left("error")

        val result = left.fold(
            onLeft  = { -1 },
            onRight = { it * 2 }
        )

        assertEquals(-1, result)
    }

    // ---------- Edge Cases ----------

    @Test
    fun testEitherSafeDivideWithLargeNumbers() {
        val result = math.safeDivide(Int.MAX_VALUE, 1)
        assertTrue(result is Either.Right)
        assertEquals(Int.MAX_VALUE, (result as Either.Right).value)
    }

    @Test
    fun testEitherSafeDivideWithMinimumInt() {
        val result = math.safeDivide(Int.MIN_VALUE, 1)
        assertTrue(result is Either.Right)
        assertEquals(Int.MIN_VALUE, (result as Either.Right).value)
    }

    @Test
    fun testEitherSafeDivideWithNegativeDividend() {
        val result = math.safeDivide(-10, 2)
        assertTrue(result is Either.Right)
        assertEquals(-5, (result as Either.Right).value)
    }

    // ---------- Property-Based Test ----------

    @Test
    fun testEitherDivisionProperty() {
        val a = 10
        val b = 2

        math.safeDivide(a, b).fold(
            onLeft  = { fail("Should not error for valid division") },
            onRight = { quotient -> assertEquals(a, quotient * b) }
        )
    }
}