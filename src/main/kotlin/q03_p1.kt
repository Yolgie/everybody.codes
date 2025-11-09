package q03.p01

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q03_p1.txt"

private const val testInput: String = """
10,5,1,10,3,8,5,2,2
"""

private const val testSolution: String = "29"


fun main() {

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
    val crates = input.split(",").map { it.trim().toInt() }
    return crates.toSet().sum().toString()
}
