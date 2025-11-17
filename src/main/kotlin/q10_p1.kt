package q10.p01

import EverybodyCodesDownloader
import TestCase
import event
import parsePackage
import kotlin.time.measureTimedValue

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
...SSS.......
.S......S.SS.
..S....S...S.
..........SS.
..SSSS...S...
.....SS..S..S
SS....D.S....
S.S..S..S....
....S.......S
.SSS..SS.....
.........S...
.......S....S
SS.....S..S..
                """,
        parameters = mapOf("movesToCheck" to "3"),
        expectedOutput = "27"
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
        println("Test ran correctly -> $testResult in $testTime ms")
    }

    testCases.forEach { runTestCase(it) }

    val (result, time) = measureTimedValue { solve(everybodyCodes.getInput().trim()) }
    println("$result in $time ms")
    everybodyCodes.submit(result)
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val movesToCheck = parameters?.get("movesToCheck")?.toInt() ?: 4

    val dragonRange = generateSequence(setOf(Coordinate(0, 0))) { coordinates ->
        getDragonMoves(coordinates)
    }.take(movesToCheck + 1).last()

    val sheep = convertBoardToCoordinates(input.lines(), 'D', 'S')

    val sheepKilledByDragon = sheep.count { it in dragonRange }

    return sheepKilledByDragon.toString()
}

typealias Coordinate = Pair<Int, Int>

// maybe alternative way to calc this is create the bounding box of +3 each direction and only check the empty ones if they could be reached
private fun getDragonMoves(start: Set<Coordinate>): Set<Coordinate> =
    start.map { coordinate -> getDragonMoves(coordinate) }.flatten().toSet()

private fun getDragonMoves(start: Coordinate): Set<Coordinate> =
    setOf(
        start,
        Coordinate(start.first + 2, start.second - 1),
        Coordinate(start.first + 2, start.second + 1),
        Coordinate(start.first - 2, start.second + 1),
        Coordinate(start.first - 2, start.second - 1),
        Coordinate(start.first + 1, start.second - 2),
        Coordinate(start.first + 1, start.second + 2),
        Coordinate(start.first - 1, start.second + 2),
        Coordinate(start.first - 1, start.second - 2)
    )

private fun convertBoardToCoordinates(board: List<String>, zero: Char, target: Char): Set<Coordinate> {
    val zeroCoordinate = board.mapIndexedNotNull { index, line ->
        if (line.contains(zero)) index to line.indexOf(zero) else null
    }.single()
    return board
        .mapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> if (c == target) x to y else null }
        }.flatten()
        .toSet()
        .map {
            it.first - zeroCoordinate.first to it.second - zeroCoordinate.second
        }.toSet()
}