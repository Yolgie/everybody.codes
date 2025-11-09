package q04.p03

import java.math.BigInteger

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q04_p3.txt"

private const val testInput: String = """
5
7|21
18|36
27|27
10|50
10|50
11
"""

private const val testSolution: String = "6818"


fun main() {

    val testResult: String = solve(testInput)
    if (testResult == testSolution) {
        println("Test ran correctly -> $testResult")
        val fileContent = Thread.currentThread().contextClassLoader.getResource(inputFileName)?.readText()!!
        println(solve(fileContent))
        println("2147483647 was wrong")
    } else {
        println("Solution was not correct: $testResult != $testSolution")
    }
}

private fun solve(input: String): String {
    val lines = input.lines().filter { it.isNotBlank() }
    val first = lines.first().trim().toInt()
    val last = lines.last().trim().toInt()
    val middleGears = lines.drop(1).dropLast(1).map { it.split("|").map { it.trim().toInt() } }.flatten()
    val gears = listOf(first) + middleGears + last

    var num = BigInteger.ONE
    var den = BigInteger.ONE

    gears
        .windowed(2, 2)
        .forEach { (driver, driven) ->
            num *= driver.toBigInteger()
            den *= driven.toBigInteger()
            val gcd = num.gcd(den)
            if (gcd != BigInteger.ONE) {
                num /= gcd
                den /= gcd
            }
        }

    val result = (BigInteger.valueOf(100) * num) / den
    return result.toString()
}
