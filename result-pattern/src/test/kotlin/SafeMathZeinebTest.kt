import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import org.example.zeineb.*

class SafeMathZeinebTest {

    @Test
    fun testNonZeroIntFromPositive() {
        NonZeroInt.from(5).match(
            onOk = { assertEquals(5, it.unwrap()) },
            onErr = { fail("Expected Ok, got Err: $it") }
        )
    }

    @Test
    fun testNonZeroIntFromZero() {
        NonZeroInt.from(0).match(
            onOk = { fail("Expected Err, got Ok: $it") },
            onErr = { assertEquals(NonZeroInputError.WasZero, it) }
        )
    }

    @Test
    fun testSafeDivideValid() {
        safeDivide(10, 2).match(
            onOk = { assertEquals(5, it) },
            onErr = { fail("Expected Ok, got Err: $it") }
        )
    }

    @Test
    fun testSafeDivideZero() {
        safeDivide(10, 0).match(
            onOk = { fail("Expected Err, got Ok: $it") },
            onErr = { assertEquals(DivisionError.DivisionByZero, it) }
        )
    }

    @Test
    fun testSafeRemValid() {
        safeRem(17, 5).match(
            onOk = { assertEquals(2, it) },
            onErr = { fail("Expected Ok, got Err: $it") }
        )
    }

    @Test
    fun testSafeRemZero() {
        safeRem(17, 0).match(
            onOk = { fail("Expected Err, got Ok: $it") },
            onErr = { assertEquals(ModuloError.ModuloByZero, it) }
        )
    }

    @Test
    fun testOperatorDiv() {
        val divisor = NonZeroInt.unsafe(3)
        assertEquals(33, 100 / divisor)
    }

    @Test
    fun testOperatorRem() {
        val divisor = NonZeroInt.unsafe(3)
        assertEquals(1, 100 % divisor)
    }
}
