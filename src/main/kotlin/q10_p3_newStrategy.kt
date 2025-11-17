package q10.p03_newStrategy

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

    val initialBoardState = BoardState(dragonStart, sheepStart)
    val boardSettings = BoardSettings(hidingPlaces, boardSize)

    return countWinningSequences(mapOf(initialBoardState to 1L), boardSettings = boardSettings).toString()
}

tailrec fun countWinningSequences(
    oldFrontier: Map<BoardState, Long>,
    oldWins: Long = 0L,
    boardSettings: BoardSettings
): Long {
    if (oldFrontier.isEmpty()) return oldWins

    val (winsThisRound, nextFrontier) =
        oldFrontier.entries.fold(0L to mutableMapOf<BoardState, Long>()) { (wins, frontier), (state, waysHere) ->
            when {
                state.sheepEscaped -> wins to frontier
                state.sheep.isEmpty() -> (wins + waysHere) to frontier
                else -> {
                    val sheepMoves = doSheepMove(state, boardSettings).ifEmpty { listOf(state) }

                    val dragonMoves = sheepMoves.flatMap { boardAfterSheep ->
                        doDragonMove(boardAfterSheep, boardSettings)
                    }

                    dragonMoves.forEach { nextState ->
                        frontier[nextState] = (frontier[nextState] ?: 0L) + waysHere
                    }

                    wins to frontier
                }
            }
        }

    return countWinningSequences(nextFrontier, oldWins + winsThisRound, boardSettings)
}


private fun doSheepMove(boardState: BoardState, boardSettings: BoardSettings): List<BoardState> {
    return boardState.sheep.mapNotNull { sheep ->
        val newSheepCoordinate = getSheepMove(sheep)
        if (newSheepCoordinate == boardState.dragon && newSheepCoordinate !in boardSettings.hidingPlaces) return@mapNotNull null
        val newSheepState = boardState.sheep - sheep + newSheepCoordinate
        boardState.copy(
            sheep = newSheepState,
            sheepEscaped = isSheepSafe(newSheepCoordinate, boardSettings)
        )
    }
}

private fun doDragonMove(boardState: BoardState, boardSettings: BoardSettings): List<BoardState> {
    return getDragonMoves(boardState.dragon)
        .mapNotNull { newDragonCoordinate ->
            if (isOutsideBoard(newDragonCoordinate, boardSettings)) return@mapNotNull null
            val sheepAfterDragonMove =
                boardState.sheep.filter { it in boardSettings.hidingPlaces || it != newDragonCoordinate }.toSet()
            boardState.copy(dragon = newDragonCoordinate, sheep = sheepAfterDragonMove)
        }
}

typealias Coordinate = Pair<Int, Int>

data class BoardSettings(
    val hidingPlaces: Set<Coordinate>,
    val boardSize: Pair<Coordinate, Coordinate>,
) {
    val smallest = boardSize.first
    val biggest = boardSize.second
}

data class BoardState(
    val dragon: Coordinate,
    val sheep: Set<Coordinate>,
    val sheepEscaped: Boolean = false,
)

private fun isOutsideBoard(coordinate: Coordinate, boardSettings: BoardSettings): Boolean =
    coordinate.first !in boardSettings.boardSize.first.first..boardSettings.boardSize.second.first
            || coordinate.second !in boardSettings.boardSize.first.second..boardSettings.boardSize.second.second


private fun isSheepSafe(newSheepCoordinate: Coordinate, boardSettings: BoardSettings): Boolean =
    coordinateRange(newSheepCoordinate, (boardSettings.biggest.first to newSheepCoordinate.second))
        .all { it in boardSettings.hidingPlaces }

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

private fun getSheepMove(start: Coordinate): Coordinate =
    Coordinate(start.first + 1, start.second)

private fun convertBoardToCoordinates(board: List<String>, target: Char): Set<Coordinate> {
    return board
        .mapIndexed { x, line ->
            line.mapIndexedNotNull { y, c -> if (c == target) x + 1 to y + 1 else null }
        }.flatten()
        .toSet()
}

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
