import java.util.concurrent.atomic.AtomicInteger

fun solveDay10(input: List<String>): Solution {
    val topographicalMap = input.map { it.toList().map { char -> char.digitToInt() }.toIntArray() }
    val rows = topographicalMap.size
    val cols = topographicalMap[0].size
    val startingPositions = topographicalMap
        .flatMapIndexed { r, line -> line.mapIndexed { c, elevation -> Pair(Vec2i(c, r), elevation) } }
        .filter { it.second == 0 }
        .map { it.first }
    val trailheadCount = countAllTrailheads(topographicalMap, cols, rows, startingPositions)
    val trailCount = countAllDistinctTrails(topographicalMap, cols, rows, startingPositions)
    return Solution(trailheadCount, trailCount)
}

private fun countAllTrailheads(topographicalMap: List<IntArray>, cols: Int, rows: Int, startingPositions: List<Vec2i>): Int {
    return startingPositions.sumOf { countAllTrails(topographicalMap, cols, rows, it) }
}

fun countAllDistinctTrails(topographicalMap: List<IntArray>, cols: Int, rows: Int, startingPositions: List<Vec2i>): Int {
    return startingPositions.sumOf { countAllDistinctTrails(topographicalMap, cols, rows, it) }
}

private fun countAllTrails(topographicalMap: List<IntArray>, cols: Int, rows: Int, startingPosition: Vec2i): Int {
    val trailEnds = mutableSetOf<Vec2i>()
    searchTrail(topographicalMap, cols, rows, startingPosition, 1) { trailEnds += it }
    return trailEnds.size
}

private fun countAllDistinctTrails(topographicalMap: List<IntArray>, cols: Int, rows: Int, startingPosition: Vec2i): Int {
    val trailCount = AtomicInteger()
    searchTrail(topographicalMap, cols, rows, startingPosition, 1) { trailCount.incrementAndGet() }
    return trailCount.get()
}

private val neighbors = listOf(
    Vec2i( 1,  0),
    Vec2i( 0, -1),
    Vec2i(-1,  0),
    Vec2i( 0,  1),
)

private fun searchTrail(
    topographicalMap: List<IntArray>,
    cols: Int,
    rows: Int,
    startingPosition: Vec2i,
    heightTarget: Int,
    callback: (Vec2i) -> Unit
) {
    for (neighbor in neighbors) {
        val newPosition = startingPosition + neighbor
        if (newPosition.x in 0 until cols && newPosition.y in 0 until rows) {
            val neighborHeight = topographicalMap[newPosition.y][newPosition.x]
            if (neighborHeight == heightTarget) {
                if (heightTarget == 9) {
                    callback(newPosition)
                } else {
                    searchTrail(topographicalMap, cols, rows, newPosition, heightTarget + 1, callback)
                }
            }
        }
    }
}
