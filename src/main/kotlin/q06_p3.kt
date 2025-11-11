package q06.p03

import java.math.BigInteger

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q06_p3.txt"

private const val testInput: String = """
AABCBABCABCabcabcABCCBAACBCa
"""

private const val testSolution: String = "3442321"
private const val testDistance = 1000
private const val testRepeat = 1000


fun main() {

    val testResult: String = solve(testInput, testDistance, testRepeat)
    if (testResult == testSolution) {
        println("Test ran correctly -> $testResult")
        val fileContent = Thread.currentThread().contextClassLoader.getResource(inputFileName)?.readText()!!
        println(solve(fileContent, 1000, 1000))
        println("1581806 was wrong")
    } else {
        println("Solution was not correct: $testResult != $testSolution")
    }
}

private fun solve(input: String, maxDistance: Int, repeat: Int): String {
    val completeInput = input.trim().repeat(repeat)
    val mentors = listOf('A', 'B', 'C')
    val mentees = listOf('a', 'b', 'c')
    val count = mutableMapOf('a' to BigInteger.ZERO, 'b' to BigInteger.ZERO, 'c' to BigInteger.ZERO)

    // initialize mentor map
    val initialMentorMap =
        completeInput.substring(0, maxDistance).filter { it in mentors }.groupingBy { it }.eachCount()

    completeInput.scanIndexed(initialMentorMap.toMutableMap()) { index, mentorMap, current ->
//        val prev = completeInput.substring(kotlin.math.max(0, index - maxDistance), index)
//        val next = completeInput.substring(kotlin.math.min(index + 1, completeInput.length-1), kotlin.math.min(completeInput.length-1, index + maxDistance + 1))

        if (index >= maxDistance + 1) {
            val prevOption = completeInput[index - maxDistance - 1]
            if (prevOption in mentors) {
                mentorMap[prevOption] = mentorMap[prevOption]!! - 1
            }
        }
        if (index + maxDistance <= completeInput.length - 1) {
            val nextOption = completeInput[index + maxDistance]
            if (nextOption in mentors) {
                mentorMap[nextOption] = mentorMap[nextOption]!! + 1
            }
        }
        if (current in mentees) {
            count[current] = count[current]!! + mentorMap[current.uppercaseChar()]!!.toBigInteger()
        }
        mentorMap
    }
    return count.values.fold(BigInteger.ZERO, BigInteger::add).toString()
}

