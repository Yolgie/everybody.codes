package q05.p01

private const val inputFileName: String = "inputfiles/everybody_codes_e2025_q05_p1.txt"

private const val testInput: String = """
58:5,3,7,8,9,10,4,5,7,8,8
"""

private const val testSolution: String = "581078"


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
    val fishboneParts = input.split(":").last().split(",").map { it.trim().toInt() }

    return fishboneParts
        .drop(1)
        .fold(listOf(FishboneSegment(center = fishboneParts.first())) ) { segments, next ->
            addToSegments(segments, next)
        }
        .map { it.center }
        .joinToString("")
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