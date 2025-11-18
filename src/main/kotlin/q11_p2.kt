package q11.p02

import EverybodyCodesDownloader
import TestCase
import event
import parsePackage
import kotlin.time.measureTimedValue

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
9
1
1
4
9
6
                """,
        expectedOutput = "11"
    ),
    TestCase(
        input = """
805
706
179
48
158
150
232
885
598
524
423
                """,
        expectedOutput = "1579"
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
    val ducksPerColumn = input.lines().map { it.toInt() }
    val duckComparatorIndices = (0..ducksPerColumn.size-1).zipWithNext()

    var roundsUsedUntilBalanced = 0
    val resultPhase1 = generateSequence(ducksPerColumn) { currentDucksPerColumn ->
        val newDucksPerColumn = currentDucksPerColumn.toMutableList()
        duckComparatorIndices.map { (first, second) ->
            if (newDucksPerColumn[first] > newDucksPerColumn[second]) {
                newDucksPerColumn[first]--
                newDucksPerColumn[second]++
            }
        }
        if (newDucksPerColumn == currentDucksPerColumn) null else {
            roundsUsedUntilBalanced++
            newDucksPerColumn
        }
    }.last()

    val resultPhase2 = generateSequence(resultPhase1) { currentDucksPerColumn ->
        val newDucksPerColumn = currentDucksPerColumn.toMutableList()
        duckComparatorIndices.map { (first, second) ->
            if (newDucksPerColumn[first] < newDucksPerColumn[second]) {
                newDucksPerColumn[first]++
                newDucksPerColumn[second]--
            }
        }
        if (newDucksPerColumn == currentDucksPerColumn) null else {
            roundsUsedUntilBalanced++
            newDucksPerColumn
        }
    }.last()

    return roundsUsedUntilBalanced.toString()
}
