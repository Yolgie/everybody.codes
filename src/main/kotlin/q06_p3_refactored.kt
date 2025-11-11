package q06.p03

import EverybodyCodesDownloader
import java.math.BigInteger
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
                    AABCBABCABCabcabcABCCBAACBCa
                """,
        expectedOutput = "34",
        parameters = mapOf("maxDistance" to "10", "repeat" to "1")
    ),
    TestCase(
        input = """
                    AABCBABCABCabcabcABCCBAACBCa
                """,
        expectedOutput = "72",
        parameters = mapOf("maxDistance" to "10", "repeat" to "2")
    ),
    TestCase(
        input = """
                    AABCBABCABCabcabcABCCBAACBCa
                """,
        expectedOutput = "3442321",
        parameters = mapOf("maxDistance" to "1000", "repeat" to "1000")
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 6, 3)
    everybodyCodes.download()

    fun runTestCase(testCase: TestCase) {
        val (testResult, testTime) = measureTimedValue {
            solve(testCase.input.trim(), testCase.parameters)
        }
        check(testResult == testCase.expectedOutput) { "Solution was not correct: $testResult != $testCases.expectedOutput" }
        println("Test ran correctly -> $testResult in $testTime ms")
    }

    testCases.forEach { runTestCase(it) }

    val (result, time) = measureTimedValue { solve(everybodyCodes.getInput().trim()) }
    println("$result in $time ms")
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val repeat = parameters?.get("repeat")?.toInt() ?: 1000
    val maxDistance = parameters?.get("maxDistance")?.toInt() ?: 1000

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