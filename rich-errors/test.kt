// Traditional sealed class approach
sealed class MathResult<out T> {
    data class Success<T>(val value: T) : MathResult<T>()
    data class Error(val reason: String) : MathResult<Nothing>()
}

fun divideWithResult(a: Int, b: Int): MathResult<Int> {
    return if (b == 0) {
        MathResult.Error("Division by zero")
    } else {
        MathResult.Success(a / b)
    }
}

fun main(){
    // Usage
    when (val result = divideWithResult(10, 2)) {
        is MathResult.Success -> println(result.value)
        is MathResult.Error -> println("Error: ${result.reason}")
    }
}