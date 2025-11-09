package q05.p02

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q05_p2.txt"

private const val testInput: String = """
1:2,4,1,1,8,2,7,9,8,6
2:7,9,9,3,8,3,8,8,6,8
3:4,7,6,9,1,8,3,7,2,2
4:6,4,2,1,7,4,5,5,5,8
5:2,9,3,8,3,9,5,2,1,4
6:2,4,9,6,7,4,1,7,6,8
7:2,3,7,6,2,2,4,1,4,2
8:5,1,5,6,8,3,1,8,3,9
9:5,7,7,3,7,2,3,8,6,7
10:4,1,9,3,8,5,4,3,5,5
"""

private const val testSolution: String = "77053"


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
    val swords = input.lines().filter { it.isNotBlank() }.map { parseSword(it) }

    val sortedSwords = swords.sortedBy { it.value }

    return (sortedSwords.last().value - sortedSwords.first().value).toString()
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
}

private data class Sword(
    val identifier: Int,
    val value: Long,
    val fishboneSegments: List<FishboneSegment>,
    val input: String
)