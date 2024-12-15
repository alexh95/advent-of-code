fun solveDay14(input: List<String>): Solution {
    val bots = input.map { it.split(" ").map { s -> s.substring(2).split(",").parseToVec2i() } }
    val safetyFactor = simulateAndCalculateSafetyFactor(bots)
    val theTree = findTheTree(bots)
    return Solution(safetyFactor, theTree)
}

private const val MAX_X = 101
private const val MAX_Y = 103
private const val MID_X = MAX_X / 2
private const val MID_Y = MAX_Y / 2
private const val SIM_STEPS = 100
private const val TIME_TO_FIND_THAT_DAMN_TREE = 10000

private fun simulateAndCalculateSafetyFactor(bots: List<List<Vec2i>>): Int {
    return bots.asSequence()
        .map { moveBot(it[0], it[1] * SIM_STEPS) }
        .map { findQuadrant(it) }
        .filter { it > 0 }
        .groupBy { it }
        .map { it.value.size }
        .fold(1) { acc, quadrantCount -> acc * quadrantCount }

}

private fun findTheTree(bots: List<List<Vec2i>>): Int {
    var positions = bots.map { it[0] }
    val velocities = bots.map { it[1] }
    repeat(TIME_TO_FIND_THAT_DAMN_TREE) {
        positions = positions.zip(velocities).map { (p, v) -> moveBot(p, v) }
        val largest = largestObject(positions)
        if (largest > 100) {
//            println(largest)
//            println(it + 1)
//            println()
//            println(botMap(positions))
            return it + 1
        }
    }
    return 0
}

private fun botMap(positions: List<Vec2i>): String {
    val positionToBotCount = positions.groupBy { it }.mapValues { it.value.size }
    return (0 until MAX_Y).map { y -> (0 until MAX_X).map { x ->
        val pos = Vec2i(x, y)
        if (positionToBotCount.contains(pos)) positionToBotCount[pos].toString() else "."
    }.joinToString("") }.joinToString("\n")
}

private fun largestObject(positions: List<Vec2i>): Int {
    val visited = (0 until MAX_Y).map { BooleanArray(MAX_X) }
    val positionSet = positions.toSet()
    var objetSizes = mutableListOf<Int>()
    for (r in 0 until MAX_Y) {
        for (c in 0 until MAX_X) {
            val pos = Vec2i(c, r)
            if (!visited[r][c]) {
                objetSizes += floodFill(positionSet, visited, pos)
            }
        }
    }
    return objetSizes.max()
}

private fun floodFill(positions: Set<Vec2i>, visited: List<BooleanArray>, pos: Vec2i): Int {
    var shapeSize = 1
    visited[pos.y][pos.x] = true
    for (r in (pos.y - 1) .. (pos.y + 1)) {
        for (c in (pos.x - 1) .. (pos.x + 1)) {
            val neighborPosition = Vec2i(c, r)
            if (positions.contains(neighborPosition)) {
                if (!visited[r][c]) {
                    shapeSize += floodFill(positions, visited, neighborPosition)
                }
            }
        }
    }
    return shapeSize
}

private fun moveBot(position: Vec2i, velocity: Vec2i): Vec2i {
    val newPosition = position + velocity
    val wrappedPosition = Vec2i(wrap(newPosition.x, MAX_X), wrap(newPosition.y, MAX_Y))
    return wrappedPosition
}

private fun wrap(value: Int, maxValue: Int): Int {
    return when {
        value >= maxValue -> value % maxValue
        value < 0 -> (maxValue + (value % maxValue)) % maxValue
        else -> value
    }
}

private fun findQuadrant(point: Vec2i): Int {
    if (point.x == MID_X || point.y == MID_Y) {
        return 0
    }
    val quadrantX = if (point.x > MID_X) 1 else 0
    val quadrantY = if (point.y > MID_Y) 2 else 0
    return 1 + quadrantX + quadrantY
}
