package q02.p03

import java.math.BigInteger

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q02_p3.txt"

private const val testInput: String = """
A=[35300,-64910]
"""

private const val testSolution: String = "406954"

private data class ComplexBigNumber(val x: BigInteger, val y: BigInteger) {
    operator fun plus(other: ComplexBigNumber): ComplexBigNumber =
        ComplexBigNumber(x.plus(other.x), y.plus(other.y))

    operator fun times(other: ComplexBigNumber): ComplexBigNumber =
        ComplexBigNumber(x.times(other.x).minus(y.times(other.y)), x.times(other.y).plus(y.times(other.x)))

    operator fun div(other: ComplexBigNumber): ComplexBigNumber =
        ComplexBigNumber(x.div(other.x), y.div(other.y))

    fun square(): ComplexBigNumber =
        ComplexBigNumber(x.times(x).minus(y.times(y)), x.times(y).plus(y.times(x)))

    override fun toString(): String = "[$x,$y]"
}

fun main() {
    val testResult: String = solve(testInput)
    if (testResult == testSolution) {
        println("Test ran correctly -> $testResult")
        val fileContent = Thread.currentThread().contextClassLoader.getResource(inputFileName)?.readText()!!
        println(solve(fileContent))
    } else {
        println("Solution was not correct: $testResult != $testSolution")
    }
}

private fun solve(input: String): String {
    val regex = """\[\s*(-?\d+)\s*,\s*(-?\d+)\s*]""".toRegex()
    val (first, second) = regex.find(input)!!.destructured
    val start = ComplexBigNumber(BigInteger(first), BigInteger(second))
    val end = start + ComplexBigNumber(BigInteger.valueOf(1000), BigInteger.valueOf(1000))
    println("start = $start, end = $end")

    val stepX = (end.x - start.x) / BigInteger.valueOf(1001 - 1)
    val stepY = (end.y - start.y) / BigInteger.valueOf(1001 - 1)
    val step = ComplexBigNumber(stepX, stepY)
    println("step = $step")

    return gridPoints(start, end, step)
        .filter { shouldBeEngraved(it) }
        .count()
        .toString()
}

private fun gridPoints(
    start: ComplexBigNumber,
    end: ComplexBigNumber,
    step: ComplexBigNumber
): Sequence<ComplexBigNumber> =
    bigIntRange(start.y, end.y, step.y).flatMap { y ->
        bigIntRange(start.x, end.x, step.x).map { x ->
            ComplexBigNumber(x, y)
        }
    }

private val engravingDivisor = ComplexBigNumber(BigInteger.valueOf(100000), BigInteger.valueOf(100000))

private fun shouldBeEngraved(coordinate: ComplexBigNumber): Boolean {
    try {
        var current = ComplexBigNumber(BigInteger.ZERO, BigInteger.ZERO)
        (0 until 100).forEach {
            val firstStep =  current.square()
            val secondStep = firstStep / engravingDivisor
            val thirdStep = secondStep + coordinate
            if (engravingExceedsBounds(thirdStep)) return false
            current = thirdStep
        }
    } catch (_: ArithmeticException) {
        return false
    }
    return true
}

private val upperLimit = BigInteger.valueOf(1000000)
private val lowerLimit = upperLimit.negate()

private fun engravingExceedsBounds(complexBigNumber: ComplexBigNumber): Boolean {
    return (complexBigNumber.x > upperLimit
            || complexBigNumber.y > upperLimit
            || complexBigNumber.x < lowerLimit
            || complexBigNumber.y < lowerLimit)
}

private fun bigIntRange(start: BigInteger, end: BigInteger, step: BigInteger): Sequence<BigInteger> =
    generateSequence(start) { current ->
        val next = current.plus(step)
        if (next > end) null else next
    }
