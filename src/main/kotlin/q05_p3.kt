package q05.p03

import kotlin.math.min

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q05_p3.txt"

private const val testInput: String = """
1:7,1,9,1,6,9,8,3,7,2
2:7,1,9,1,6,9,8,3,7,2
"""

private const val testSolution: String = "4"


fun main() {

    val testResult: String = solve(testInput)
    if (testResult == testSolution) {
        println("Test ran correctly -> $testResult")
        val fileContent = Thread.currentThread().contextClassLoader.getResource(inputFileName)?.readText()!!
        println(solve(fileContent))
        println("31000659 was wrong (correct length, correct start)")
    } else {
        println("Solution was not correct: $testResult != $testSolution")
    }
}

private fun solve(input: String): String {
    val swords = input.lines().filter { it.isNotBlank() }.map { parseSword(it) }

    val sortedSwords = swords.sortedDescending()

    return sortedSwords.mapIndexed { index, sword -> (index + 1) * sword.identifier }.sum().toString()
}

private fun parseSword(input: String) : Sword {
    val (id, segmentInput) = input.split(":")
    val segments = getSegments(segmentInput.split(",").map { it.trim().toInt() })
    val value = getValue(segments)
    return Sword(
        identifier = id.trim().toInt(),
        value = value,
        fishboneSegments = segments,
        input = input
    )
}

private fun getSegments(segmentInput: List<Int>) : List<FishboneSegment> {
    return segmentInput
        .drop(1)
        .fold(listOf(FishboneSegment(center = segmentInput.first())) ) { segments, next ->
            addToSegments(segments, next)
        }
}

private fun getValue(fishboneSegments: List<FishboneSegment>) : Long {
    return fishboneSegments
        .map { it.center }
        .joinToString("")
        .toLong()
}

private fun addToSegments(segments: List<FishboneSegment>, next: Int) : List<FishboneSegment> {
    segments.forEach {
        if (it.left == null && next < it.center) {
            it.left = next
            return segments
        } else if (it.right == null && next > it.center) {
            it.right = next
            return segments
        }
    }
    return segments + FishboneSegment(center = next)
}

private data class FishboneSegment(var left: Int? = null, val center: Int, var right: Int? = null) {
    override fun toString(): String = "${left ?: ""} -- $center -- ${right ?: ""}"
    fun value(): Int = "${left ?: ""}$center${right ?: ""}".toInt()
}

private data class Sword (
    val identifier: Int,
    val value: Long,
    val fishboneSegments: List<FishboneSegment>,
    val input: String
) : Comparable<Sword> {
    override fun compareTo(other: Sword): Int {
        if (this.value != other.value) return this.value.compareTo(other.value)

        (0 until min(this.fishboneSegments.size, other.fishboneSegments.size)).forEach {
            if (this.fishboneSegments[it].value() != other.fishboneSegments[it].value()) {
                return this.fishboneSegments[it].value().compareTo(other.fishboneSegments[it].value())
            }
        }

        return this.identifier.compareTo(other.identifier)
    }

}