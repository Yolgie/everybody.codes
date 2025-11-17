
const val event = "2025"

fun parsePackage(input: String): Pair<Int, Int> {
    val regex = """q(\d+)\.p(\d+)""".toRegex()
    val (q, p) = regex.find(input)!!.destructured
    return q.toInt() to p.toInt()
}

data class TestCase(val input: String, val parameters: Map<String, String>? = null, val expectedOutput: String)
