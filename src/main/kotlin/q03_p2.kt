package q03.p02

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q03_p2.txt"

private const val testInput: String = """
4,51,13,64,57,51,82,57,16,88,89,48,32,49,49,2,84,65,49,43,9,13,2,3,75,72,63,48,61,14,40,77
"""

private const val testSolution: String = "781"


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
    return crates
        .toSortedSet().also { println(it) }
        .take(20).also { println(it) }
        .sum()
        .toString()
}
