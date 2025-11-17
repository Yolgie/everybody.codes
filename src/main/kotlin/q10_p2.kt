package q10.p02

import EverybodyCodesDownloader
import TestCase
import event
import parsePackage
import kotlin.time.measureTimedValue

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
...SSS##.....
.S#.##..S#SS.
..S.##.S#..S.
.#..#S##..SS.
..SSSS.#.S.#.
.##..SS.#S.#S
SS##.#D.S.#..
S.S..S..S###.
.##.S#.#....S
.SSS.#SS..##.
..#.##...S##.
.#...#.S#...S
SS...#.S.#S..
                """,
        parameters = mapOf("movesToCheck" to "1"),
        expectedOutput = "5"
    ),
    TestCase(
        input = """
...SSS##.....
.S#.##..S#SS.
..S.##.S#..S.
.#..#S##..SS.
..SSSS.#.S.#.
.##..SS.#S.#S
SS##.#D.S.#..
S.S..S..S###.
.##.S#.#....S
.SSS.#SS..##.
..#.##...S##.
.#...#.S#...S
SS...#.S.#S..
                """,
        parameters = mapOf("movesToCheck" to "2"),
        expectedOutput = "12"
    ),
    TestCase(
        input = """
...SSS##.....
.S#.##..S#SS.
..S.##.S#..S.
.#..#S##..SS.
..SSSS.#.S.#.
.##..SS.#S.#S
SS##.#D.S.#..
S.S..S..S###.
.##.S#.#....S
.SSS.#SS..##.
..#.##...S##.
.#...#.S#...S
SS...#.S.#S..
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
    val board = input.lines()
    val movesToCheck = parameters?.get("movesToCheck")?.toInt() ?: 20

    val dragonStart = setOf(Coordinate(0, 0))
    val sheepStart = convertBoardToCoordinates(board, 'D', 'S')
    val hidingPlaces = convertBoardToCoordinates(board, 'D', '#')
    val (smallest, biggest) = convertBoardToSize(board, zero = 'D')

    val finalBoardState = generateSequence(BoardState(dragonStart, sheepStart)) { boardState ->
//        println("before turn")
//        visualizeBoard(boardState, hidingPlaces, smallest to biggest)
        val dragonsMove = getDragonMoves(boardState.dragons).filterBoardSize(smallest, biggest)
//        println("after dragons")
//        visualizeBoard(BoardState(dragonsMove, boardState.sheep), hidingPlaces, smallest to biggest)
        val sheepAfterDragonMoves = boardState.sheep.filter { sheep -> sheep in hidingPlaces || sheep !in dragonsMove }.toSet()
        val eatenAfterDragonMoves = boardState.sheep.count() - sheepAfterDragonMoves.count()
        val sheepMoves = getSheepMoves(sheepAfterDragonMoves).filterBoardSize(smallest, biggest)
//        println("after sheep")
//        visualizeBoard(BoardState(dragonsMove, sheepMoves), hidingPlaces, smallest to biggest)
        val sheepAfterSheepMoves = sheepMoves.filter { sheep -> sheep in hidingPlaces || sheep !in dragonsMove }.toSet()
        val eatenAfterSheepMoves = sheepMoves.count() - sheepAfterSheepMoves.count()
        BoardState(dragonsMove, sheepAfterSheepMoves, boardState.sheepKilledByDragons + eatenAfterDragonMoves + eatenAfterSheepMoves)
    }.take(movesToCheck + 1).last()

    return finalBoardState.sheepKilledByDragons.toString()
}


typealias Coordinate = Pair<Int, Int>
data class BoardState(val dragons: Set<Coordinate>, val sheep: Set<Coordinate>, val sheepKilledByDragons: Int = 0)

// maybe alternative way to calc this is create the bounding box of +3 each direction and only check the empty ones if they could be reached
private fun getDragonMoves(start: Set<Coordinate>): Set<Coordinate> =
    start.map { coordinate -> getDragonMoves(coordinate) }.flatten().toSet()

private fun getDragonMoves(start: Coordinate): Set<Coordinate> =
    setOf(
        Coordinate(start.first + 2, start.second - 1),
        Coordinate(start.first + 2, start.second + 1),
        Coordinate(start.first - 2, start.second + 1),
        Coordinate(start.first - 2, start.second - 1),
        Coordinate(start.first + 1, start.second - 2),
        Coordinate(start.first + 1, start.second + 2),
        Coordinate(start.first - 1, start.second + 2),
        Coordinate(start.first - 1, start.second - 2)
    )

private fun getSheepMoves(start: Set<Coordinate>): Set<Coordinate> =
    start.map { Coordinate(it.first + 1, it.second) }.toSet()

private fun Set<Coordinate>.filterBoardSize(smallest: Coordinate, biggest: Coordinate) : Set<Coordinate> =
    this.filter { it.first in smallest.first..biggest.first && it.second in smallest.second..biggest.second }.toSet()

private fun convertBoardToCoordinates(board: List<String>, zero: Char, target: Char): Set<Coordinate> {
    val zeroCoordinate = board.mapIndexedNotNull { index, line ->
        if (line.contains(zero)) index to line.indexOf(zero) else null
    }.single()
    return board
        .mapIndexed { x, line ->
            line.mapIndexedNotNull { y, c -> if (c == target) x to y else null }
        }.flatten()
        .toSet()
        .map {
            it.first - zeroCoordinate.first to it.second - zeroCoordinate.second
        }.toSet()
}

private fun convertBoardToSize(board: List<String>, zero: Char) : Pair<Coordinate, Coordinate> {
    val zeroCoordinate = board.mapIndexedNotNull { index, line ->
        if (line.contains(zero)) index to line.indexOf(zero) else null
    }.single()
    val smallest = -zeroCoordinate.first to -zeroCoordinate.second
    val biggest = board.size - zeroCoordinate.first - 1 to board[0].length - zeroCoordinate.second - 1
    return smallest to biggest
}

private fun visualizeBoard(boardState: BoardState, hidingPlaces: Set<Coordinate>, boardSize: Pair<Coordinate, Coordinate>) {
    for (x in boardSize.first.first..boardSize.second.first) {
        for (y in boardSize.first.second..boardSize.second.second) {
            when (Coordinate(x, y)) {
                in boardState.dragons intersect boardState.sheep intersect hidingPlaces -> print('x')
                in boardState.dragons intersect boardState.sheep -> print('X')
                in boardState.dragons intersect hidingPlaces -> print('d')
                in boardState.sheep intersect hidingPlaces -> print('s')
                in boardState.dragons -> print('D')
                in boardState.sheep -> print('S')
                in hidingPlaces -> print('#')
                else -> print('.')
            }
        }
        println()
    }
}