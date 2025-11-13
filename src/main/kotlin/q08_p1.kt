package q08.p01

import EverybodyCodesDownloader
import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
1,5,2,6,8,4,1,7,3
                """,
        parameters = mapOf("nails" to "8"),
        expectedOutput = "4"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 8, 1)
    everybodyCodes.download()

    fun runTestCase(testCase: TestCase) {
        val (testResult, testTime) = measureTimedValue {
            solve(testCase.input.trim(), testCase.parameters)
        }
        check(testResult == testCase.expectedOutput) { "Solution was not correct: $testResult != ${testCase.expectedOutput}" }
        println("Test ran correctly -> $testResult in $testTime ms")
    }

    testCases.forEach { runTestCase(it) }

    val (result, time) = measureTimedValue { solve(everybodyCodes.getInput().trim()) }
    println("$result in $time ms")
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val nails = parameters?.get("nails")?.toInt() ?: 32
    val path = input.trim().split(",").map { it.toInt() }
    val halfDistance = nails / 2

    return path.zipWithNext().filter { (first, second) ->
        (first - second).absoluteValue == halfDistance
    }.count().toString()
}
