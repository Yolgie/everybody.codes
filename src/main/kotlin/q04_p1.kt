package q04.p01

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q04_p1.txt"

private const val testInput: String = """
102
75
50
35
13
"""

private const val testSolution: String = "15888"


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
    val gears = input.lines().filter { !it.isBlank() } .map { it.trim().toInt() }.map { it.toDouble() }

    return gears.zipWithNext()
        .fold(2025.toDouble()) { turns, (driver, driven) ->
            turns * driver / driven
        }
        .toInt()
        .toString()
}
