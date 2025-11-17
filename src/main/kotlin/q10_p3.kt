package q10.p03

import EverybodyCodesDownloader
import TestCase
import event
import parsePackage
import kotlin.time.measureTimedValue

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
SSS
..#
#.#
#D.
                """,
        expectedOutput = "15"
    ),
    TestCase(
        input = """
SSS
..#
..#
.##
.D#
                """,
        expectedOutput = "8"
    ),
    TestCase(
        input = """
..S..
.....
..#..
.....
..D..
                """,
        expectedOutput = "44"
    ),
    TestCase(
        input = """
.SS.S
#...#
...#.
##..#
.####
##D.#
                """,
        expectedOutput = "4406"
    ),
    TestCase(
        input = """
SSS.S
.....
#.#.#
.#.#.
#.D.#
                """,
        expectedOutput = "13033988838"
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
    val board = input.lines()

    val dragonStart = convertBoardToCoordinates(board, 'D').single()
    val sheepStart = convertBoardToCoordinates(board, 'S')
    val hidingPlaces = convertBoardToCoordinates(board, '#')
    val boardSize = convertBoardToSize(board)

    val initialBoardState = BoardState(dragonStart, sheepStart, hidingPlaces, boardSize)
    var count = 0L

    val finalBoardState = iterateUntilStable(listOf(initialBoardState)) {
        it.flatMap { oldBoardState ->
            if (oldBoardState.sheepEscaped) return@flatMap emptyList()
            if (oldBoardState.sheep.count() == 0) {
                count++
                return@flatMap emptyList()
            }

            val sheepMove: Iterable<BoardState> = doSheepMove(oldBoardState).ifEmpty { listOf(oldBoardState) }
            val dragonMoves: Iterable<BoardState> = sheepMove.flatMap { boardStateAfterSheepMove ->
                doDragonMove(boardStateAfterSheepMove)
            }
            dragonMoves
        }
    }

//    println("\n\n\nFinal State\n")
//    finalBoardState.sortedBy { it.move.length }.forEach { println(it) }

    return count.toString()
}

private fun doSheepMove(boardState: BoardState): List<BoardState> {
    return boardState.sheep.mapNotNull { sheep ->
        val newSheepCoordinate = getSheepMove(sheep)
        if (newSheepCoordinate == boardState.dragon && newSheepCoordinate !in boardState.hidingPlaces) return@mapNotNull null
        val newSheepState = boardState.sheep - sheep + newSheepCoordinate
        boardState.copy(
            sheep = newSheepState,
            sheepEscaped = boardState.isSafe(newSheepCoordinate)
        )
    }
}

private fun doDragonMove(boardState: BoardState): List<BoardState> {
    return getDragonMoves(boardState.dragon)
        .mapNotNull { newDragonCoordinate ->
            if (boardState.isOutsideBoard(newDragonCoordinate)) return@mapNotNull null
            val sheepAfterDragonMove =
                boardState.sheep.filter { it in boardState.hidingPlaces || it != newDragonCoordinate }.toSet()
            boardState.copy(dragon = newDragonCoordinate, sheep = sheepAfterDragonMove)
        }
}

typealias Coordinate = Pair<Int, Int>

data class BoardState(
    val dragon: Coordinate,
    val sheep: Set<Coordinate>,
    val hidingPlaces: Set<Coordinate>,
    val boardSize: Pair<Coordinate, Coordinate>,
    val sheepEscaped: Boolean = false,
) {
    fun isSafe(newSheepCoordinate: Coordinate): Boolean =
        coordinateRange(newSheepCoordinate, (biggest.first to newSheepCoordinate.second)).all { it in hidingPlaces }

    fun isOutsideBoard(coordinate: Coordinate): Boolean =
        coordinate.first !in boardSize.first.first..boardSize.second.first || coordinate.second !in boardSize.first.second..boardSize.second.second

    val smallest = boardSize.first
    val biggest = boardSize.second
}

data class Move(val move: String, val boardState: BoardState) {
    override fun toString(): String =
        "$move -> D:${boardState.dragon} S(${boardState.sheep.count()}):${boardState.sheep} ${if (boardState.sheepEscaped) "Escaped" else ""}"
}

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
    start.map { getSheepMove(it) }.toSet()

private fun getSheepMove(start: Coordinate): Coordinate =
    Coordinate(start.first + 1, start.second)

private fun Set<Coordinate>.filterBoardSize(smallest: Coordinate, biggest: Coordinate): Set<Coordinate> =
    this.filter { it.first in smallest.first..biggest.first && it.second in smallest.second..biggest.second }.toSet()

private fun convertBoardToCoordinates(board: List<String>, target: Char): Set<Coordinate> {
    return board
        .mapIndexed { x, line ->
            line.mapIndexedNotNull { y, c -> if (c == target) x + 1 to y + 1 else null }
        }.flatten()
        .toSet()
}

private fun convertCoordinateToMove(coordinate: Coordinate): String =
    "${('A'.code + coordinate.second - 1).toChar()}${coordinate.first}"

private fun convertBoardToSize(board: List<String>): Pair<Coordinate, Coordinate> {
    val smallest = 1 to 1
    val biggest = board.size to board[0].length
    return smallest to biggest
}

private fun visualizeBoard(
    boardState: BoardState,
    hidingPlaces: Set<Coordinate>,
    boardSize: Pair<Coordinate, Coordinate>
) {
    for (x in boardSize.first.first..boardSize.second.first) {
        for (y in boardSize.first.second..boardSize.second.second) {
            when (Coordinate(x, y)) {
                in setOf(boardState.dragon) intersect boardState.sheep intersect hidingPlaces -> print('x')
                in setOf(boardState.dragon) intersect boardState.sheep -> print('X')
                in setOf(boardState.dragon) intersect hidingPlaces -> print('d')
                in boardState.sheep intersect hidingPlaces -> print('s')
                boardState.dragon -> print('D')
                in boardState.sheep -> print('S')
                in hidingPlaces -> print('#')
                else -> print('.')
            }
        }
        println()
    }
}

private fun <T> iterateUntilStable(seed: T, step: (T) -> T): T {
    var current = seed
    while (true) {
        val next = step(current)
        if (next == current) return current
        current = next
    }
}

private fun coordinateRange(from: Coordinate, to: Coordinate): Sequence<Coordinate> {
    if (from.first > to.first || from.second > to.second) return emptySequence()

    return (from.first..to.first).asSequence().flatMap { x ->
        (from.second..to.second).asSequence().map { y -> Coordinate(x, y) }
    }
}

private fun joinMoves(move1: String?, move2: String?): String = when {
    move1.isNullOrBlank() -> move2 ?: ""
    move2.isNullOrBlank() -> move1
    else -> "$move1 $move2"
}