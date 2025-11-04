private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q01_p2.txt"

private const val testInput: String = """
Vyrdax,Drakzyph,Fyrryn,Elarzris

R3,L2,R3,L1
"""

private const val testSolution: String = "Elarzris"

fun main() {
    val testResult: String = solve(testInput)
    if (testResult == testSolution) {
        val fileContent = Thread.currentThread().contextClassLoader.getResource(inputFileName)?.readText()!!
        println(solve(fileContent))
    } else {
        println("Solution was not correct: $testResult != $testSolution")
    }
}

private fun solve(input: String): String {
    val inputLines = input.lines().filter { it.isNotBlank() }

    val names = inputLines.first().split(",")
    val instructions = inputLines.last().split(",")

    val resultIndex = instructions.fold(0) { index: Int, instruction: String ->
        println("index = $index, instruction = $instruction")
        if (instruction.startsWith("L")) {
            (index - instruction.drop(1).toInt() + names.size) % names.size
        } else {
            (index + instruction.drop(1).toInt()) % names.size
        }
    }
    return names[resultIndex]
}