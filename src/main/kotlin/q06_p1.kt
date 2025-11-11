package q06.p01

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q06_p1.txt"

private const val testInput: String = """
ABabACacBCbca
"""

private const val testSolution: String = "5"


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
    var count = 0

    input.scan("") { acc, current ->
        if (current == 'a') {
            count += acc.count { it == 'A' }
        }

        acc + current
    }
    return count.toString()
}