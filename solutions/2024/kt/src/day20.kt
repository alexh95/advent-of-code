fun solveDay20(input: List<String>): Solution {
    val levelSize = Vec2i(input[0].length, input.size)
    val (startingPosition, endingPosition) = getStartAndEndPositions(input)
    val (distance, path) = findPathCost(input, levelSize, startingPosition, endingPosition)
    val usefulCheatCount = cheat(distance, path, levelSize, 2)
    val usefulBigCheatCount = cheat(distance, path, levelSize, 20)
    return Solution(usefulCheatCount, usefulBigCheatCount)
}

private fun cheat(distance: List<IntArray>, path: List<Vec2i>, levelSize: Vec2i, cheatLength: Int): Int {
    val cheatPositions = calculateCheatPositions(cheatLength)
    val allBigCheat = path.flatMap { cheatCandidate(distance, levelSize, cheatPositions, it) }
    return allBigCheat.count { it.second >= 100 }
}

private fun cheatCandidate(distance: List<IntArray>, levelSize: Vec2i, cheatPositions: List<Pair<Vec2i, Int>>, position: Vec2i): List<Pair<Pair<Vec2i, Vec2i>, Int>> {
    val cheatEndPositions = cheatPositions
        .map { (p, dist) -> Pair(position + p, dist) }
        .filter { (p, _) -> inBounds(levelSize, p) }
        .filter { (p, _) -> distance[p.y][p.x] < Int.MAX_VALUE }
    return cheatEndPositions
        .map { (p, dist) -> Pair(Pair(position, p), distance[position.y][position.x] - dist - distance[p.y][p.x]) }
        .filter { (_, dist) -> dist > 0 }
}

private fun calculateCheatPositions(cheatLength: Int): List<Pair<Vec2i, Int>> {
    return (-cheatLength..cheatLength)
        .flatMap { y -> (-cheatLength..cheatLength).map { x -> Vec2i(x, y) } }
        .map { Pair(it, it.distManhattan()) }
        .filter { (_, dist) -> dist <= cheatLength }
}

private fun findPathCost(input: List<String>, levelSize: Vec2i, startingPosition: Vec2i, endingPosition: Vec2i): Pair<List<IntArray>, List<Vec2i>> {
    val distance = (0 until levelSize.y).map { IntArray(levelSize.x) { Int.MAX_VALUE } }
    var cost = 0
    val path = mutableListOf<Vec2i>()
    var nextPosition = endingPosition
    distance[endingPosition.y][endingPosition.x] = cost++
    val visited = mutableSetOf(endingPosition)
    while (nextPosition != startingPosition) {
        val neighborPosition = neighborDirections.map {nextPosition + it}
            .filterNot { visited.contains(it) }
            .first { input[it.y][it.x] != '#' }
        visited.add(neighborPosition)
        distance[neighborPosition.y][neighborPosition.x] = cost++
        nextPosition = neighborPosition
        path.add(nextPosition)
    }
    return Pair(distance, path.reversed())
}

private val neighborDirections = listOf(
    Vec2i( 1,  0),
    Vec2i( 0,  1),
    Vec2i(-1,  0),
    Vec2i( 0, -1),
)

private fun getStartAndEndPositions(input: List<String>): Pair<Vec2i, Vec2i> {
    return Pair(getPosition(input, 'S'), getPosition(input, 'E'))
}

private fun getPosition(input: List<String>, c: Char): Vec2i {
    val y = input.indexOfFirst { it.contains(c) }
    val x = input[y].indexOf(c)
    return Vec2i(x, y)
}
