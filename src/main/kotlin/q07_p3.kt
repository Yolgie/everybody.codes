package q07.p03

import EverybodyCodesDownloader
import kotlin.time.measureTimedValue

private data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)

private val testCases = listOf<TestCase>(
    TestCase(
        input = """
Xaryt

X > a,o
a > r,t
r > y,e,a
h > a,e,v
t > h
v > e
y > p,t
                """,
        expectedOutput = "25"
    ),
    TestCase(
        input = """
Khara,Xaryt,Noxer,Kharax

r > v,e,a,g,y
a > e,v,x,r,g
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
        expectedOutput = "1154"
    ),
)

fun main() {
    val everybodyCodes = EverybodyCodesDownloader("2025", 7, 3)
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
    val prefixes = inputLines.first().split(",")
    val rules = input.lines().drop(1).filter { it.isNotBlank() }.map { it.split(">") }
        .map { it.first().trim().first() to it.last().split(",").map { it.trim().first() } }.toMap()

//    println("names = $prefixes")
//    println("rules = $rules")

    val validPrefixes = prefixes.filter { name ->
        name.zipWithNext().all { (first, second) -> rules[first]?.contains(second) ?: false }
    }
//    println("validPrefixes = $validPrefixes")

    val names = expandNames(rules, validPrefixes.toSet())
//    println("names = $names")

    return names.filter { it.length >= 7 && it.length <= 11 }.size.toString()

}

private tailrec fun expandNames(
    rules: Map<Char, List<Char>>,
    names: Set<String>,
): Set<String> {
    if (names.all { it.length >= 11 }) return names
    val next = names.flatMap { name ->
        if (name.length >= 11) return@flatMap listOf(name)
        rules[name.last()]?.map { name + it }?.plus(name) ?: listOf(name)
    }.toSet()
    return if (next == names) names else expandNames(rules, next)
}

