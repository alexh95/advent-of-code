import kotlin.math.abs

fun solveDay1(input: List<String>): Solution {
    val pairs = input.map { it.split("   ") }
    val left = pairs.map { it[0].toInt() }.sorted()
    val right = pairs.map { it[1].toInt() }.sorted()
    val distanceSum = orderedDistanceSum(left, right)
    val similarityScore = computeSimilarityScore(left, right)
    return Solution(distanceSum, similarityScore)
}

private fun orderedDistanceSum(left: List<Int>, right: List<Int>): Int {
    return left.zip(right).sumOf { (l, r) -> abs(l - r) }
}

private fun computeSimilarityScore(left: List<Int>, right: List<Int>): Int {
    val occurrence = right.associateBy ({ it }, { right.count { r -> it == r } })
    return left.sumOf { it * (occurrence[it] ?: 0) }
}
