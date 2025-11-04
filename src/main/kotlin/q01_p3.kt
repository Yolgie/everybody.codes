private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q01_p3.txt"

private const val testInput: String = """
Vyrdax,Drakzyph,Fyrryn,Elarzris

R3,L2,R3,L3
"""

private const val testSolution: String = "Drakzyph"

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

    val resultNamesList = instructions.fold(names) { namesList: List<String>, instruction: String ->
        println("names = ${namesList.joinToString(",")}, instruction = $instruction")
        if (instruction.startsWith("L")) {
            namesList.swap(0, safeModulo(instruction.drop(1).toInt() * -1, names.size))
        } else {
            namesList.swap(0, safeModulo(instruction.drop(1).toInt(), names.size))
        }
    }
    return resultNamesList.first()
}

private fun <T> List<T>.swap(i: Int, j: Int): List<T> =
    toMutableList().apply {
        val tmp = this[i]
        this[i] = this[j]
        this[j] = tmp
    }

private fun safeModulo(a: Int, size: Int): Int = ((a % size) + size) % size
