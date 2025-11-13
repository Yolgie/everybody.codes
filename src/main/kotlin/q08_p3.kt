package q08.p03

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
1,5,2,6,8,4,1,7,3,6
                """,
        parameters = mapOf("nails" to "8"),
        expectedOutput = "7"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 8, 3)
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
    val nails = parameters?.get("nails")?.toInt() ?: 256
    val path = input.trim().split(",").map { it.toInt() }
    val pairs = path.zipWithNext().map { pair -> pair.toSortedPair() }
    val allPossibleCuts = (1..256).asSequence().allPairs()

    val stringsCut = allPossibleCuts.map { swordSwipe ->
        pairs.count { string -> intersects(swordSwipe, string) }
    }.maxOrNull() ?: 0

    return stringsCut.toString()
}

fun <T : Comparable<T>> Pair<T, T>.toSortedPair(): Pair<T, T> =
    if (first <= second) this else second to first

fun <T : Comparable<T>> intersects(p1: Pair<T, T>, p2: Pair<T, T>): Boolean {
    val (a, b) = p1
    val (c, d) = p2
    return (a < c && c < b && b < d) || (c < a && a < d && d < b) || (a == c && b == d)
}

fun <T> Sequence<T>.allPairs(): Sequence<Pair<T, T>> =
    flatMapIndexed { i, a -> drop(i + 1).map { b -> a to b } }
