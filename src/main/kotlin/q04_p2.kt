package q04.p02

import kotlin.math.roundToLong

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q04_p2.txt"

private const val testInput: String = """
128
64
32
16
8
"""

private const val testSolution: String = "625000000000"


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
        .fold(1.toDouble()) { turns, (driver, driven) ->
            turns * driver / driven
        }
        .let { 10000000000000L / it  }
        .roundToLong()
        .toString()
}
