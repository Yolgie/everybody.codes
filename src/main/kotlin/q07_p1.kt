package q07.p01

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
Oronris,Urakris,Oroneth,Uraketh

r > a,i,o
i > p,w
n > e,r
o > n,m
k > f,r
a > k
U > r
e > t
O > r
t > h
                """,
        expectedOutput = "Oroneth"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 7, 1)
    everybodyCodes.download()

    fun runTestCase(testCase: TestCase) {
        val (testResult, testTime) = measureTimedValue {
            solve(testCase.input.trim(), testCase.parameters)
        }
        check(testResult == testCase.expectedOutput) { "Solution was not correct: $testResult != $testCases.expectedOutput" }
        println("Test ran correctly -> $testResult in $testTime ms")
    }

    testCases.forEach { runTestCase(it) }

    val (result, time) = measureTimedValue { solve(everybodyCodes.getInput().trim()) }
    println("$result in $time ms")
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val inputLines = input.lines()
    val names = inputLines.first().split(",")
    val rules = input.lines().drop(1).filter { it.isNotBlank() }.map { it.split(">") }.map { it.first().trim().first() to it.last().split(",").map { it.trim().first() }}.toMap()

    println("names = $names")
    println("rules = $rules")

    return names.filter { name -> name.zipWithNext().all { (first, second) -> rules[first]?.contains(second) ?: false }}.first()
}