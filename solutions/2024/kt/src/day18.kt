import kotlin.math.abs

fun solveDay18(input: List<String>): Solution {
    val corruptedPositions = input.map { it.split(',').parseToVec2i() }
    val level = makeLevel(corruptedPositions, CORRUPTED_BYTES)
    val minStepCount = findMinCostPath(level, STARTING_POSITION, ENDING_POSITION)
    val blockingPosition = findFirstBlockingPosition(corruptedPositions, STARTING_POSITION, ENDING_POSITION)
    return Solution(minStepCount.toString(), "${blockingPosition.x},${blockingPosition.y}")
}

private const val GRID_ROWS = 71
private const val GRID_COLS = 71
private val STARTING_POSITION = Vec2i(0, 0)
private val ENDING_POSITION = Vec2i(GRID_COLS - 1, GRID_ROWS - 1)
private const val CORRUPTED_BYTES = 1024

private fun findMinCostPath(level: List<BooleanArray>, startingPosition: Vec2i, endingPosition: Vec2i): Int {
    val cameFrom = mutableMapOf(Pair(startingPosition, startingPosition))
    val minCost = mutableMapOf(Pair(startingPosition, 0))
    val estCost = mutableMapOf(Pair(startingPosition, estimateCost(startingPosition, startingPosition)))

    val discoveredPositions = mutableSetOf(startingPosition)
    while (discoveredPositions.isNotEmpty()) {
        val currentPosition = discoveredPositions.minBy { estCost.getOrDefault(it, Int.MAX_VALUE) }
        discoveredPositions.remove(currentPosition)
        for (neighbor in passableNeighbors(level, currentPosition)) {
            val candidateEstCost = minCost[currentPosition]!! + 1
            if (candidateEstCost < minCost.getOrDefault(neighbor, Int.MAX_VALUE)) {
                cameFrom[neighbor] = currentPosition
                minCost[neighbor] = candidateEstCost
                estCost[neighbor] = candidateEstCost + estimateCost(neighbor, endingPosition)
                discoveredPositions.add(neighbor)
            }
        }
    }

    return minCost.getOrDefault(endingPosition, Int.MAX_VALUE)
}

fun findFirstBlockingPosition(corruptedPositions: List<Vec2i>, startingPosition: Vec2i, endingPosition: Vec2i): Vec2i {
    val corruptedCountToSolvable = mutableMapOf(Pair(0, true))
    var minCorrupted = 0
    var maxCorrupted = corruptedPositions.size - 1

    while (minCorrupted <= maxCorrupted) {
        val corrupted = minCorrupted + (maxCorrupted - minCorrupted) / 2
        val solvable = isSolvable(corruptedCountToSolvable, corruptedPositions, corrupted, startingPosition, endingPosition)
        val solvableRight = isSolvable(corruptedCountToSolvable, corruptedPositions, corrupted + 1, startingPosition, endingPosition)
        if (solvable) {
            if (!solvableRight) {
                return corruptedPositions[corrupted]
            } else {
                minCorrupted = corrupted + 1
            }
        } else {
            maxCorrupted = corrupted - 1
        }
    }

    return Vec2i(0, 0)
}

fun isSolvable(
    corruptedCountToSolvable: MutableMap<Int, Boolean>,
    corruptedPositions: List<Vec2i>,
    corruptedCount: Int,
    startingPosition: Vec2i,
    endingPosition: Vec2i
): Boolean {
    return corruptedCountToSolvable.getOrElse(corruptedCount) {
        val level = makeLevel(corruptedPositions, corruptedCount)
        val minPathCost = findMinCostPath(level, startingPosition, endingPosition)
        val solvable = minPathCost < Int.MAX_VALUE
        solvable.also { corruptedCountToSolvable[corruptedCount] = it }
    }
}

private fun makeLevel(corruptedPositions: List<Vec2i>, corruptedCount: Int): List<BooleanArray> {
    val level = (0 until GRID_ROWS).map { BooleanArray(GRID_COLS) }
    corruptBytes(level, corruptedPositions, corruptedCount)
    return level
}

private fun corruptBytes(level: List<BooleanArray>, positions: List<Vec2i>, corruptedCount: Int) {
    for (index in 0 until corruptedCount) {
        val pos = positions[index]
        level[pos.y][pos.x] = true
    }
}

private fun passableNeighbors(level: List<BooleanArray>, position: Vec2i): Sequence<Vec2i> {
    return neighborDirections.asSequence()
        .map { position + it }
        .filter { it.x in (0 until GRID_COLS) && it.y in (0 until GRID_COLS) }
        .filterNot { level[it.y][it.x] }
}

private val neighborDirections = listOf(
    Vec2i( 1,  0),
    Vec2i( 0,  1),
    Vec2i(-1,  0),
    Vec2i( 0, -1),
)

private fun estimateCost(startingPosition: Vec2i, endingPosition: Vec2i): Int {
    return abs(startingPosition.x - endingPosition.x) + abs(startingPosition.y - endingPosition.y)
}
