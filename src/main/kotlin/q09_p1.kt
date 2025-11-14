package q09.p01

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
1:CAAGCGCTAAGTTCGCTGGATGTGTGCCCGCG
2:CTTGAATTGGGCCGTTTACCTGGTTTAACCAT
3:CTAGCGCTGAGCTGGCTGCCTGGTTGACCGCG
                """,
//        parameters = mapOf("nails" to "8"),
        expectedOutput = "414"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 9, 1)
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
//    everybodyCodes.submit(result)
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val scales = input.lines().map { it.split(":").map { it.trim() } }.map { it[0].toInt() to it[1] }.toMap()

    val childSimilarity = scales.mapNotNull { (id, scale) ->
        val others = (scales - id).map { (_, otherScale) -> otherScale }

        if (!isChild(scale, others.first(), others.last())) return@mapNotNull null
        println("Found child: $id -> $scale")

        val parent1similarity = compareScales(others.first(), scale)
        val parent2similarity = compareScales(others.last(), scale)

        parent1similarity.count { it } * parent2similarity.count { it }
    }.single()

    return childSimilarity.toString()
}

private fun compareScales(scale1: String, scale2: String): List<Boolean> =
    scale1.zip(scale2).map { (x, y) -> x == y }

private fun isChild(child: String, parent1: String, parent2: String): Boolean {
    val parent1similarity = compareScales(parent1, child)
    val parent2similarity = compareScales(parent2, child)

    return addComparison(parent1similarity, parent2similarity).all { it }
}

private fun addComparison(a: List<Boolean>, b: List<Boolean>): List<Boolean> =
    a.zip(b).map { (x, y) -> x || y }