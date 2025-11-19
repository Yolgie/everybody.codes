package q12.p02

import EverybodyCodesDownloader
import TestCase
import event
import parsePackage
import kotlin.time.measureTimedValue

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
9589233445
9679121695
8469121876
8352919876
7342914327
7234193437
6789193538
6781219648
5691219769
5443329859
                """,
        expectedOutput = "58"
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
    val barrels = input.lines().map { it.toMutableList() }.toMutableList()
    val exploded = 'X'

    val seed = listOf(
        Coordinate(0, 0),
        Coordinate(barrels.size - 1, barrels.last().size - 1)
    )
    generateSequence(seed) { coordinates ->
        var newCoordinates = mutableListOf<Coordinate>()
        coordinates.forEach { coordinate ->
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

    return barrels.sumOf { it.count {it == exploded }}.toString()
}


typealias Coordinate = Pair<Int, Int>
typealias Barrels = MutableList<MutableList<Char>>

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