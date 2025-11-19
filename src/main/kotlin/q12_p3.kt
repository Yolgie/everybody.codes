package q12.p03

import EverybodyCodesDownloader
import TestCase
import event
import parsePackage
import kotlin.time.measureTimedValue

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
5411
3362
5235
3112
                """,
        expectedOutput = "14"
    ),
    TestCase(
        input = """
41951111131882511179
32112222211508122215
31223333322105122219
31234444432147511128
91223333322176021892
60112222211166431583
04661111166111111746
01111119042122222177
41222108881233333219
71222127839122222196
56111026279711111507
                """,
        expectedOutput = "133"
    ),
)

fun main() {
    val (quest, part) = parsePackage(object {}.javaClass.packageName)
    val everybodyCodes = EverybodyCodesDownloader(event, quest, part)
    everybodyCodes.download()

    fun runTestCase(testCase: TestCase) {
        val (testResult, testTime) = measureTimedValue {
            solve(testCase.input.trim(), testCase.parameters)
        }
        check(testResult == testCase.expectedOutput) { "Solution was not correct: $testResult != ${testCase.expectedOutput}" }
        println("Test ran correctly -> $testResult in $testTime")
    }

    testCases.forEach { runTestCase(it) }

    val (result, time) = measureTimedValue { solve(everybodyCodes.getInput().trim()) }
    println("$result in $time")
    everybodyCodes.submit(result)
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val exploded = 'X'

    val seeds = getAllSeeds(
        Coordinate(0, 0),
        Coordinate(input.lines().size - 1, input.lines().first().length - 1)
    )

    var topRoundOne: Result = calculateOptimalExplosions(seeds, input.lines(), exploded).also { println("Round 1: $it") }
    var topRoundTwo: Result = calculateOptimalExplosions(seeds, topRoundOne.barrels, exploded).also {  println("Round 2: $it") }
    var topRoundThree: Result = calculateOptimalExplosions(seeds, topRoundTwo.barrels, exploded).also {  println("Round 3: $it") }

    return getExplosionCount(listOf(topRoundOne.seed, topRoundTwo.seed, topRoundThree.seed), input.lines(), exploded).first.toString()
}

private fun calculateOptimalExplosions(
    seeds: Sequence<Coordinate>,
    input: List<String>,
    exploded: Char
): Result {
    var top: Result? = null
    seeds.forEach { seed ->
        if (input[seed.first][seed.second] == exploded) return@forEach
        val (resultSize, barrels) = getExplosionCount(listOf(seed), input, exploded)
        if (resultSize > top?.size ?: 0) {
            top = Result(resultSize, seed, barrels)
        }
    }
    return top!!
}

private data class Result(val size: Int, val seed: Coordinate, val barrels: List<String>)

private fun getExplosionCount(
    seed: List<Coordinate>,
    input: List<String>,
    exploded: Char
): Pair<Int, List<String>> {
    val barrels = input.map { it.toMutableList() }.toMutableList()
    generateSequence(seed) { coordinates ->
        var newCoordinates = mutableListOf<Coordinate>()
        coordinates.forEach { coordinate ->
            if (barrels.get(coordinate) == exploded) return@forEach
            val neighbors = barrels.getNeighbors(coordinate).filter { barrels.get(it) != exploded }
            neighbors.forEach { neighbor ->
                if (barrels.get(neighbor) <= barrels.get(coordinate)) {
                    newCoordinates.add(neighbor)
                }
            }
            barrels.set(coordinate, exploded)
        }
        newCoordinates.toSet().toList().ifEmpty { null }
    }.last()
    return barrels.sumOf { it.count {it == exploded }} to barrels.map { it.joinToString("") }
}


typealias Coordinate = Pair<Int, Int>
typealias Barrels = MutableList<MutableList<Char>>

private fun getAllSeeds(a: Coordinate, b: Coordinate): Sequence<Coordinate> {
    val minX = minOf(a.first, b.first)
    val maxX = maxOf(a.first, b.first)
    val minY = minOf(a.second, b.second)
    val maxY = maxOf(a.second, b.second)

    return (minX..maxX).asSequence().flatMap { x ->
        (minY..maxY).asSequence().map { y -> x to y }
    }
}

private fun Barrels.getNeighbors(from: Coordinate): List<Coordinate> {
    var result = mutableListOf<Coordinate>()
    if (this.isValid(from.copy(first = from.first - 1))) result.add(from.copy(first = from.first - 1))
    if (this.isValid(from.copy(first = from.first + 1))) result.add(from.copy(first = from.first + 1))
    if (this.isValid(from.copy(second = from.second - 1))) result.add(from.copy(second = from.second - 1))
    if (this.isValid(from.copy(second = from.second + 1))) result.add(from.copy(second = from.second + 1))
    return result
}

private fun Barrels.isValid(coordinate: Coordinate): Boolean =
    coordinate.first in this.indices && coordinate.second in this[coordinate.first].indices

private fun Barrels.get(coordinate: Coordinate): Char = this[coordinate.first][coordinate.second]
private fun Barrels.set(coordinate: Coordinate, value: Char) {
    this[coordinate.first][coordinate.second] = value
}