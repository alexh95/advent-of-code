fun solveDay12(input: List<String>): Solution {
    val rows = input.size
    val cols = input[0].length
    val (gardenPlots, neighborAdjacency) = extractPlots(rows, cols, input)
    val costPerGardenPlot = gardenPlots.values
        .sumOf { it.size * it.sumOf { pos -> 4 - neighborAdjacency[pos.y * cols + pos.x] } }
    val costPerBundledPlot = gardenPlots.values
        .sumOf { it.size * countPlotEdges(it) }
    return Solution(costPerGardenPlot, costPerBundledPlot)
}


private fun extractPlots(
    rows: Int,
    cols: Int,
    garden: List<String>
): Pair<Map<Int, List<Vec2i>>, IntArray> {
    val neighborAdjacency = IntArray(rows * cols)
    val gardenPlots = mutableMapOf<Int, MutableList<Vec2i>>()
    val visited = BooleanArray(rows * cols)
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            val plotIndex = r * cols + c
            if (!visited[plotIndex]) {
                countNeighbors(neighborAdjacency, visited, gardenPlots, garden, cols, rows, Vec2i(c, r), plotIndex)
            }
        }
    }
    return Pair(gardenPlots, neighborAdjacency)
}

private val directions = listOf(
    Vec2i( 1,  0),
    Vec2i( 0,  1),
    Vec2i(-1,  0),
    Vec2i( 0, -1),
)

private fun countNeighbors(
    neighborAdjacency: IntArray,
    visited: BooleanArray,
    gardenPlots: MutableMap<Int, MutableList<Vec2i>>,
    garden: List<String>,
    cols: Int,
    rows: Int,
    pos: Vec2i,
    initialPlotIndex: Int,
) {
    val plant = garden[pos.y][pos.x]
    val plotIndex = pos.y * cols + pos.x
    visited[plotIndex] = true
    gardenPlots.putIfAbsent(initialPlotIndex, mutableListOf())
    gardenPlots[initialPlotIndex]!!.add(pos)
    for (direction in directions) {
        val newPos = pos + direction
        if (newPos.x in 0 until cols && newPos.y in 0 until rows) {
            val neighborPlant = garden[newPos.y][newPos.x]
            if (plant == neighborPlant) {
                neighborAdjacency[plotIndex]++
                val neighborPlotIndex = newPos.y * cols + newPos.x
                if (!visited[neighborPlotIndex]) {
                    countNeighbors(neighborAdjacency, visited, gardenPlots, garden, cols, rows, newPos, initialPlotIndex)
                }
            }
        }
    }
}

private fun countPlotEdges(points: List<Vec2i>): Int {
    val rangeX = IntRange(points.minOf { it.x }, points.maxOf { it.x })
    val rangeY = IntRange(points.minOf { it.y }, points.maxOf { it.y })

    var edgeCount = 0
    for (y in rangeY) {
        var lastHasEdge = false
        for (x in rangeX) {
            val pos = Vec2i(x, y)
            if (pos in points) {
                val abovePos = Vec2i(x, y - 1)
                if (abovePos in points) {
                    lastHasEdge = false
                } else if (!lastHasEdge) {
                    lastHasEdge = true
                    ++edgeCount
                }
            } else {
                lastHasEdge = false
            }
        }
    }

    for (x in rangeX.reversed()) {
        var lastHasEdge = false
        for (y in rangeY) {
            val pos = Vec2i(x, y)
            if (pos in points) {
                val abovePos = Vec2i(x + 1, y)
                if (abovePos in points) {
                    lastHasEdge = false
                } else if (!lastHasEdge) {
                    lastHasEdge = true
                    ++edgeCount
                }
            } else {
                lastHasEdge = false
            }
        }
    }

    for (y in rangeY.reversed()) {
        var lastHasEdge = false
        for (x in rangeX) {
            val pos = Vec2i(x, y)
            if (pos in points) {
                val abovePos = Vec2i(x, y + 1)
                if (abovePos in points) {
                    lastHasEdge = false
                } else if (!lastHasEdge) {
                    lastHasEdge = true
                    ++edgeCount
                }
            } else {
                lastHasEdge = false
            }
        }
    }

    for (x in rangeX) {
        var lastHasEdge = false
        for (y in rangeY) {
            val pos = Vec2i(x, y)
            if (pos in points) {
                val abovePos = Vec2i(x - 1, y)
                if (abovePos in points) {
                    lastHasEdge = false
                } else if (!lastHasEdge) {
                    lastHasEdge = true
                    ++edgeCount
                }
            } else {
                lastHasEdge = false
            }
        }
    }

    return edgeCount
}
