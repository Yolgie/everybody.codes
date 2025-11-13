package q08.p02

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
1,5,2,6,8,4,1,7,3,5,7,8,2
                """,
        parameters = mapOf("nails" to "8"),
        expectedOutput = "21"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 8, 2)
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
    val pairs = path.zipWithNext().map { pair -> pair.toSortedPair() }

    val partials = pairs.runningFold(emptyList<Pair<Int, Int>>()) { pastPairs, next -> pastPairs + next }.drop(1)
    val knots = partials.fold(0) { knots, partial ->
        val past = partial.dropLast(1)
        val current = partial.last()

        val newKnots = past.filter { pastLine -> intersects(current, pastLine) }.count()

        knots + newKnots
    }

    return knots.toString()
}

fun <T : Comparable<T>> Pair<T, T>.toSortedPair(): Pair<T, T> =
    if (first <= second) this else second to first

fun <T : Comparable<T>> intersects(p1: Pair<T, T>, p2: Pair<T, T>): Boolean {
    val (a, b) = p1
    val (c, d) = p2
    return (a < c && c < b && b < d) || (c < a && a < d && d < b)
}
