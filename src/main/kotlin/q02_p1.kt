package q02.p01

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q02_p1.txt"

private const val testInput: String = """
A=[25,9]
"""

private const val testSolution: String = "[357,862]"

private data class ComplexNumber(val x: Int, val y: Int) {
    operator fun plus(other: ComplexNumber): ComplexNumber = ComplexNumber(x + other.x, y + other.y)
    operator fun times(other: ComplexNumber): ComplexNumber = ComplexNumber(x * other.x - y * other.y, x * other.y + y * other.x)
    operator fun div(other: ComplexNumber): ComplexNumber = ComplexNumber( x = x / other.x, y = y / other.y)
    override fun toString(): String = "[$x,$y]"
}

fun main() {

    test( ComplexNumber(1, 1) + ComplexNumber(2, 2) == ComplexNumber(3, 3))
    test( ComplexNumber(2, 5) + ComplexNumber(3, 7) == ComplexNumber(5, 12))
    test( ComplexNumber(-2, 5) + ComplexNumber(10, -1) == ComplexNumber(8, 4))
    test( ComplexNumber(-1, -2) + ComplexNumber(-3, -4) == ComplexNumber(-4, -6))
    test( ComplexNumber(1, 1) * ComplexNumber(2, 2) == ComplexNumber(0, 4))
    test( ComplexNumber(2, 5) * ComplexNumber(3, 7) == ComplexNumber(-29, 29))
    test( ComplexNumber(-2, 5) * ComplexNumber(10, -1) == ComplexNumber(-15, 52))
    test( ComplexNumber(-1, -2) * ComplexNumber(-3, -4) == ComplexNumber(-5, 10))
    test( ComplexNumber(10, 12) / ComplexNumber(2, 2)  == ComplexNumber(5,6))
    test( ComplexNumber(11, 12) / ComplexNumber(3, 5)  == ComplexNumber(3,2))
    test( ComplexNumber(-10, -12) / ComplexNumber(2, 2)  == ComplexNumber(-5,-6))
    test( ComplexNumber(-11, -12) / ComplexNumber(3, 5)  == ComplexNumber(-3, -2))

    val testResult: String = solve(testInput)
    if (testResult == testSolution) {
        println("Test ran correctly -> $testResult")
        val fileContent = Thread.currentThread().contextClassLoader.getResource(inputFileName)?.readText()!!
        println(solve(fileContent))
    } else {
        println("Solution was not correct: $testResult != $testSolution")
    }
}

private fun solve(input: String): String {
    val regex = """\[(\d+),\s*(\d+)]""".toRegex()
    val (first, second) = regex.find(input)!!.destructured
    val start = ComplexNumber(first.toInt(), second.toInt())

    val result = generateSequence(start) { current ->
        ((current * current) / ComplexNumber(10,10)) + start
    }

    return result.take(3).onEach { println(it) }.last().toString()
}



private fun test(condition: Boolean) {
    if (!condition) throw AssertionError("Test failed!")
}
