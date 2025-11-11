package q06.p02

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q06_p2.txt"

private const val testInput: String = """
ABabACacBCbca
"""

private const val testSolution: String = "11"


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
    var countA = 0
    var countB = 0
    var countC = 0

    input.scan("") { acc, current ->
        if (current == 'a') {
            countA += acc.count { it == 'A' }
        }
        if (current == 'b') {
            countB += acc.count { it == 'B' }
        }
        if (current == 'c') {
            countC += acc.count { it == 'C' }
        }

        acc + current
    }
    return (countA+countB+countC).toString()
}
