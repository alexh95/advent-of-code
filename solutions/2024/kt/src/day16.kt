import kotlin.math.abs

fun solveDay16(input: List<String>): Solution {
    val (startingPosition, endingPosition) = getStartAndEndPositions(input)
    val (minCost, cameFrom) = findMinCostPath(input, startingPosition, endingPosition)
    val minPathCost = minCost[endingPosition]!!
    val bestPathTileCount = countBestPathTiles(minCost, cameFrom, endingPosition)
//    printCostMap(input, minCost, cameFrom)
    return Solution(minPathCost, bestPathTileCount)
}

private fun findMinCostPath(level: List<String>, startingPosition: Vec2i, endingPosition: Vec2i): Pair<Map<Vec2i, Int>, Map<Vec2i, Vec2i>> {
    val cameFrom = mutableMapOf(Pair(startingPosition, startingPosition - directions[0]))
    val minCost = mutableMapOf(Pair(startingPosition, 0))
    val estCost = mutableMapOf(Pair(startingPosition, estimateCost(startingPosition, startingPosition)))

    val discoveredPos = mutableSetOf(Pair(startingPosition, directions[0]))
    while (discoveredPos.isNotEmpty()) {
        val current = discoveredPos.minBy { estCost.getOrDefault(it.first, Int.MAX_VALUE) }
        val (position, direction) = current
        discoveredPos.remove(current)
        for ((neighborPos, dirToNeighbor) in neighborsOf(level, position)) {
            val candidateEstCost = minCost[position]!! + realCost(direction, dirToNeighbor)
            if (candidateEstCost < minCost.getOrDefault(neighborPos, Int.MAX_VALUE)) {
                cameFrom[neighborPos] = position
                minCost[neighborPos] = candidateEstCost
                estCost[neighborPos] = candidateEstCost + estimateCost(neighborPos, endingPosition)
                discoveredPos.add(Pair(neighborPos, dirToNeighbor))
            }
        }
    }
    return Pair(minCost, cameFrom)
}

private fun countBestPathTiles(minCost: Map<Vec2i, Int>, cameFrom: Map<Vec2i, Vec2i>, endingPosition: Vec2i): Int {
    val goesTo = mutableMapOf<Vec2i, Vec2i>()
    val discoveredPos = mutableSetOf(endingPosition)
    val pointingAtPos = mutableSetOf(endingPosition)
    while (discoveredPos.isNotEmpty()) {
        val position = discoveredPos.first()
        discoveredPos.remove(position)
        val from = cameFrom[position]!!
        if (cameFrom.contains(from)) {
            pointingAtPos.add(from)
            discoveredPos.add(from)
            goesTo[from] = position
        }
        val nextPosition = goesTo.getOrDefault(position, endingPosition)
        val nextPositionCost = minCost[nextPosition]!!

        val neighborsPointingAtPosition = directions.asSequence().map { position + it }
            .filter { !pointingAtPos.contains(it) && cameFrom.contains(it) && cameFrom.contains(cameFrom[it]) }
            .map { Pair(it, it - cameFrom[it]!!) }
            .filter { (neighborPos, neighborDirection) -> neighborPos + neighborDirection == position }
            .map { it.first }
            .filter { minCost[it]!! < nextPositionCost }
            .toList()
        pointingAtPos.addAll(neighborsPointingAtPosition)
        discoveredPos.addAll(neighborsPointingAtPosition)
        neighborsPointingAtPosition.forEach { goesTo[it] = position }
    }
    return pointingAtPos.size
}

private fun realCost(direction: Vec2i, dirToNeighbor: Vec2i): Int {
    return if (direction == dirToNeighbor) 1 else 1001
}

private fun estimateCost(from: Vec2i, to: Vec2i): Int {
    val absDiffX = abs(from.x - to.x)
    val absDiffY = abs(from.y - to.y)
    return absDiffX + absDiffY + if (absDiffX == 0 && absDiffY == 0) 0 else 1000
}

private fun neighborsOf(level: List<String>, position: Vec2i): List<Pair<Vec2i, Vec2i>> {
    return directions
        .map { Pair(position + it, it) }
        .filter { level[it.first.y][it.first.x] != '#' }
}

private val directions = listOf(
    Vec2i( 1,  0),
    Vec2i( 0,  1),
    Vec2i(-1,  0),
    Vec2i( 0, -1),
)

private fun getStartAndEndPositions(level: List<String>): Pair<Vec2i, Vec2i> {
    val startingPositionY = level.indexOfFirst { it.contains('S') }
    val startingPositionX = level[startingPositionY].indexOf('S')
    val startingPosition = Vec2i(startingPositionX, startingPositionY)

    val endingPositionY = level.indexOfFirst { it.contains('E') }
    val endingPositionX = level[endingPositionY].indexOf('E')
    val endingPosition = Vec2i(endingPositionX, endingPositionY)
    return Pair(startingPosition, endingPosition)
}

private fun printCostMap(input: List<String>, minCost: Map<Vec2i, Int>, cameFrom: Map<Vec2i, Vec2i>) {
    val costMapString = input.indices.joinToString("\n") { r ->
        (0 until input[0].length).joinToString("") { c ->
            val p = Vec2i(c, r)
            if (minCost.contains(p)) {
                String.format("%05d %c ", minCost[p], dirToChar(p - cameFrom[p]!!))
            } else {
                "####### "
            }
        }
    }
    println(costMapString)
}

private fun dirToChar(dir: Vec2i): Char {
    return when (dir) {
        directions[0] -> '>'
        directions[1] -> 'V'
        directions[2] -> '<'
        directions[3] -> '^'
        else -> '?'
    }
}
