// Tests without JUnit dependencies

fun main() {
    // Run tests
    runAllTests()
    
    println("\n" + "=".repeat(50))
    println("ORIGINAL PROGRAM")
    println("=".repeat(50) + "\n")
    
    // Original program code...
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

fun runAllTests() {
    println("=".repeat(50))
    println("RUNNING TESTS")
    println("=".repeat(50))
    
    val math = SafeMath()
    var passed = 0
    var failed = 0
    
    // Test 1
    runTest("NonZeroInt.from(5)") {
        val result = SafeMath.NonZeroInt.from(5)
        assert(result is Result.Ok)
        assert((result as Result.Ok).value.unwrap() == 5)
    }.let { if (it) passed++ else failed++ }
    
    // Test 2
    runTest("NonZeroInt.from(0)") {
        val result = SafeMath.NonZeroInt.from(0)
        assert(result is Result.Err)
        assert((result as Result.Err).error == SafeMath.NonZeroInputError.WasZero)
    }.let { if (it) passed++ else failed++ }
    
    // Test 3
    runTest("safeDivide(10, 2)") {
        val result = math.safeDivide(10, 2)
        assert(result is Result.Ok)
        assert((result as Result.Ok).value == 5)
    }.let { if (it) passed++ else failed++ }
    
    // Test 4
    runTest("safeDivide(10, 0)") {
        val result = math.safeDivide(10, 0)
        assert(result is Result.Err)
        assert((result as Result.Err).error == SafeMath.DivideByZero)
    }.let { if (it) passed++ else failed++ }
    
    // Test 5
    runTest("safeModulo(10, 3)") {
        val result = math.safeModulo(10, 3)
        assert(result is Result.Ok)
        assert((result as Result.Ok).value == 1)
    }.let { if (it) passed++ else failed++ }
    
    println("\nTest Summary: ✅ $passed passed, ❌ $failed failed")
}

fun runTest(name: String, test: () -> Unit): Boolean {
    return try {
        print("Testing: $name... ")
        test()
        println("✅ PASSED")
        true
    } catch (e: AssertionError) {
        println("❌ FAILED: ${e.message}")
        false
    }
}