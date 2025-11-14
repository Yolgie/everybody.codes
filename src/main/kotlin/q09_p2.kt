package q09.p02

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
1:GCAGGCGAGTATGATACCCGGCTAGCCACCCC
2:TCTCGCGAGGATATTACTGGGCCAGACCCCCC
3:GGTGGAACATTCGAAAGTTGCATAGGGTGGTG
4:GCTCGCGAGTATATTACCGAACCAGCCCCTCA
5:GCAGCTTAGTATGACCGCCAAATCGCGACTCA
6:AGTGGAACCTTGGATAGTCTCATATAGCGGCA
7:GGCGTAATAATCGGATGCTGCAGAGGCTGCTG
                """,
//        parameters = mapOf("nails" to "8"),
        expectedOutput = "1245"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 9, 2)
//    everybodyCodes.download()

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
    everybodyCodes.submit(result)
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val scales = input.lines()
        .map { it.split(":").map { it.trim() } }
        .map { it[0].toInt() to it[1] }
        .map { (id, scale) -> id to DragonDuck(id, scale) }
        .toMap()

    val result = scales.asSequence()
        .mapNotNull { (_, dragonDuck) ->
            val others = (scales - dragonDuck.first)
                .asSequence()
                .map { it.value }
                .allUnorderedPairs()

            val childSimilarityResult = others.mapNotNull { parents -> isChild(dragonDuck, parents) }.singleOrNull()
            if (childSimilarityResult != null) println("Found child: ${childSimilarityResult.child.first} -> ${childSimilarityResult.parents.map { it.first }} : ${childSimilarityResult.degreeOfSimilarity}")
            childSimilarityResult
        }


    return result.sumOf { it.degreeOfSimilarity }.toString()
}

private fun isChild(child: DragonDuck, parents: List<DragonDuck>): ChildSimilarityResult? {
    assert(parents.size == 2)
    val parentsDna = parents.map { it.second }
    val comparison = child.second.mapIndexed { index, childDnaElement ->
        if ( parentsDna.none { it[index] == childDnaElement } ) return null
        val comparison = parentsDna.map { it[index] == childDnaElement }
//        println(comparison)
        comparison
    }.transpose()
//    println(child.second)
//    println(parents.first().second)
//    println(comparison.first().map { if (it) "+" else " " }.joinToString(""))
//    println(comparison.first().count {it})
//    println(child.second)
//    println(parents.last().second)
//    println(comparison.last().map { if (it) "+" else " " }.joinToString(""))
//    println(comparison.last().count {it})
//    println(comparison.first().count {it} * comparison.last().count {it})

    val degreeOfSimilarity = comparison.fold(1) { acc, next -> acc * next.count {it} }
//    println(degreeOfSimilarity)

    return ChildSimilarityResult(child, parents, degreeOfSimilarity)
}

private fun <T> Sequence<T>.allUnorderedPairs(): Sequence<List<T>> =
    mapIndexed { i, a -> i to a }
        .flatMap { (i, a) ->
            drop(i + 1).map { b -> listOf(a, b) }
        }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    require(isNotEmpty())
    val cols = this[0].size
    return List(cols) { c -> List(size) { r -> this[r][c] } }
}

typealias DragonDuck = Pair<Int, String>
data class ChildSimilarityResult(val child: DragonDuck, val parents: List<DragonDuck>, val degreeOfSimilarity: Int)
