import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import org.example.zeineb.*

class SafeMathZeinebTest {

    @Test
    fun testNonZeroIntFromPositive() {
        with(SafeMath) {
            SafeMath.NonZeroInt.from(5).match(
                onOk = { assertEquals(5, it.unwrap()) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testNonZeroIntFromZero() {
        with(SafeMath) {
            SafeMath.NonZeroInt.from(0).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.NonZeroInputError.WasZero, it) }
            )
        }
    }

    @Test
    fun testSafeDivideValidInt() {
        with(SafeMath) {
            safeDivide(10, 2).match(
                onOk = { assertEquals(5, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeDivideZeroInt() {
        with(SafeMath) {
            safeDivide(10, 0).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.DivisionError.DivisionByZero, it) }
            )
        }
    }

    @Test
    fun testSafeDivideValidLong() {
        with(SafeMath) {
            safeDivide(100L, 4L).match(
                onOk = { assertEquals(25L, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeDivideZeroLong() {
        with(SafeMath) {
            safeDivide(100L, 0L).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.DivisionError.DivisionByZero, it) }
            )
        }
    }

    @Test
    fun testSafeDivideValidUInt() {
        with(SafeMath) {
            safeDivide(20u, 5u).match(
                onOk = { assertEquals(4u, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeDivideZeroUInt() {
        with(SafeMath) {
            safeDivide(20u, 0u).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.DivisionError.DivisionByZero, it) }
            )
        }
    }

    @Test
    fun testSafeDivideValidULong() {
        with(SafeMath) {
            safeDivide(200UL, 8UL).match(
                onOk = { assertEquals(25UL, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeDivideZeroULong() {
        with(SafeMath) {
            safeDivide(200UL, 0UL).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.DivisionError.DivisionByZero, it) }
            )
        }
    }

    @Test
    fun testSafeRemValidInt() {
        with(SafeMath) {
            safeRem(17, 5).match(
                onOk = { assertEquals(2, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeRemZeroInt() {
        with(SafeMath) {
            safeRem(17, 0).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.ModuloError.ModuloByZero, it) }
            )
        }
    }

    @Test
    fun testSafeRemValidLong() {
        with(SafeMath) {
            safeRem(100L, 7L).match(
                onOk = { assertEquals(2L, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeRemZeroLong() {
        with(SafeMath) {
            safeRem(100L, 0L).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.ModuloError.ModuloByZero, it) }
            )
        }
    }

    @Test
    fun testSafeRemValidUInt() {
        with(SafeMath) {
            safeRem(25u, 6u).match(
                onOk = { assertEquals(1u, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeRemZeroUInt() {
        with(SafeMath) {
            safeRem(25u, 0u).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.ModuloError.ModuloByZero, it) }
            )
        }
    }

    @Test
    fun testSafeRemValidULong() {
        with(SafeMath) {
            safeRem(150UL, 11UL).match(
                onOk = { assertEquals(7UL, it) },
                onErr = { fail("Expected Ok, got Err: $it") }
            )
        }
    }

    @Test
    fun testSafeRemZeroULong() {
        with(SafeMath) {
            safeRem(150UL, 0UL).match(
                onOk = { fail("Expected Err, got Ok: $it") },
                onErr = { assertEquals(SafeMath.ModuloError.ModuloByZero, it) }
            )
        }
    }

    @Test
    fun testOperatorDivInt() {
        with(SafeMath) {
            val divisor = SafeMath.NonZeroInt.unsafe(3)
            assertEquals(33, 100.div(divisor))
        }
    }

    @Test
    fun testOperatorRemInt() {
        with(SafeMath) {
            val divisor = SafeMath.NonZeroInt.unsafe(3)
            assertEquals(1, 100.rem(divisor))
        }
    }

    @Test
    fun testOperatorDivLong() {
        with(SafeMath) {
            val divisor = SafeMath.NonZeroInt.unsafe(4)
            assertEquals(25L, 1000L.div(divisor))
        }
    }

    @Test
    fun testOperatorRemLong() {
        with(SafeMath) {
            val divisor = SafeMath.NonZeroInt.unsafe(4)
            assertEquals(0L, 1000L.rem(divisor))
        }
    }
}
