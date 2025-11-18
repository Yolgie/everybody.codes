package q11.p01

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
        parameters = mapOf("rounds" to "10"),
        expectedOutput = "109"
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
    val rounds = parameters?.get("rounds")?.toInt() ?: 10
    val ducksPerColumn = input.lines().map { it.toInt() }
    println(ducksPerColumn)
    val duckComparatorIndices = (0..ducksPerColumn.size-1).zipWithNext()
    println(duckComparatorIndices)

    println("Phase 1")
    var roundsUsedForPhase1 = 0
    val resultPhase1 = generateSequence(ducksPerColumn) { currentDucksPerColumn ->
        val newDucksPerColumn = currentDucksPerColumn.toMutableList()
        duckComparatorIndices.map { (first, second) ->
            if (newDucksPerColumn[first] > newDucksPerColumn[second]) {
                newDucksPerColumn[first]--
                newDucksPerColumn[second]++
            }
        }
        println("$newDucksPerColumn     ${newDucksPerColumn.getFlockChecksum()}")
        if (newDucksPerColumn == currentDucksPerColumn) null else {
            roundsUsedForPhase1++
            newDucksPerColumn
        }
    }.last()

    println("Phase 2")
    val resultPhase2 = generateSequence(resultPhase1) { currentDucksPerColumn ->
        val newDucksPerColumn = currentDucksPerColumn.toMutableList()
        duckComparatorIndices.map { (first, second) ->
            if (newDucksPerColumn[first] < newDucksPerColumn[second]) {
                newDucksPerColumn[first]++
                newDucksPerColumn[second]--
            }
        }
        println("$newDucksPerColumn     ${newDucksPerColumn.getFlockChecksum()}")
        if (newDucksPerColumn == currentDucksPerColumn) null else newDucksPerColumn
    }.take(rounds-roundsUsedForPhase1+1).last()

    return resultPhase2.getFlockChecksum().toString()
}

private fun List<Int>.getFlockChecksum(): Int = this.mapIndexed { index, numberOfDucks -> (index + 1) * numberOfDucks }.sum()
