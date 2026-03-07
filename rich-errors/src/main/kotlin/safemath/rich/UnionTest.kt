package safemath.rich

sealed class MathError : Error() {
    object DivisionByZero : MathError()
}

sealed class DivideResult {
    data class Success(val value: Int) : DivideResult()
    data class Failure(val error: MathError) : DivideResult()
}

fun divide(a: Int, b: Int): DivideResult {
    return if (b == 0) {
        DivideResult.Failure(MathError.DivisionByZero)
    } else {
        DivideResult.Success(a / b)
    }
}

fun main() {
    println("Kotlin version: ${KotlinVersion.CURRENT}")
    println("Java version: ${System.getProperty("java.version")}")
    println("Java home: ${System.getProperty("java.home")}")
    
    try {
        println("Starting test...")
        
        // Test 1: Basic divide
        val result1 = divide(10, 2)
        println("Test 1 - Result: $result1")
        
        // Test 2: Division by zero
        val result2 = divide(10, 0)
        println("Test 2 - Result: $result2")
        
        println("Tests completed")
        
    } catch (e: Throwable) {
        println("❌ CAUGHT EXCEPTION: ${e::class.simpleName}")
        println("Message: ${e.message}")
        e.printStackTrace(System.out)
    }
}
