package q07.p02

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
Xanverax,Khargyth,Nexzeth,Helther,Braerex,Tirgryph,Kharverax

r > v,e,a,g,y
a > e,v,x,r
e > r,x,v,t
h > a,e,v
g > r,y
y > p,t
i > v,r
K > h
v > e
B > r
t > h
N > e
p > h
H > e
l > t
z > e
X > a
n > v
x > z
T > i
                """,
        expectedOutput = "23"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 7, 2)
    //everybodyCodes.download()

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
}

private fun solve(input: String, parameters: Map<String, String>? = null): String {
    val inputLines = input.lines()
    val names = inputLines.first().split(",")
    val rules = input.lines().drop(1).filter { it.isNotBlank() }.map { it.split(">") }
        .map { it.first().trim().first() to it.last().split(",").map { it.trim().first() } }.toMap()

    println("names = $names")
    println("rules = $rules")

    val correctNames = names.mapIndexed { index, name ->
        if (name.zipWithNext().all { (first, second) -> rules[first]?.contains(second) ?: false }) index+1 to name else null
    }.filterNotNull()
    println("correctNames = $correctNames")

    return correctNames.map { it.first }.sum().toString()
}