fun solveDay6(input: List<String>): Solution {
    val maxY = input.size
    val maxX = input[0].length
    val startingPosition = input
        .mapIndexed { index, line -> Pair(line.indexOf("^"), index) }
        .filter { it.first > -1 }
        .pairToVec2i()
    val uniquePositions = simulateUniquePositions(input, maxX, maxY, startingPosition)
    val tilesCovered = uniquePositions.size
    val loopingBlockCount = blockGuard(input, maxX, maxY, startingPosition, uniquePositions)
    return Solution(tilesCovered, loopingBlockCount)
}

private val directions = listOf(
    Vec2i( 0, -1),
    Vec2i( 1,  0),
    Vec2i( 0,  1),
    Vec2i(-1,  0),
)

private fun simulateUniquePositions(level: List<String>, maxX: Int, maxY: Int, startingPosition: Vec2i): Set<Vec2i> {
    var position = startingPosition
    var direction = 0
    val uniquePositions = mutableSetOf(position)
    var notDone = true
    while (notDone) {
        val delta = directions[direction]
        val newPosition = position + delta
        if (newPosition.x !in 0 until maxX || newPosition.y !in 0 until maxY) {
            notDone = false
        } else {
            val c = level[newPosition.y][newPosition.x]
            if (c == '#') {
                direction = (direction + 1) % 4
            } else {
                position = newPosition
                uniquePositions += position
            }
        }
    }
    return uniquePositions
}

private fun simulateLooping(level: List<String>, maxX: Int, maxY: Int, startingPosition: Vec2i, positionToBlock: Vec2i): Boolean {
    var position = startingPosition
    var direction = 0
    var notDone = true
    val previousStates = mutableSetOf(Pair(direction, position))
    while (notDone) {
        val delta = directions[direction]
        val newPosition = position + delta
        if (newPosition.x !in 0 until maxX || newPosition.y !in 0 until maxY) {
            notDone = false
        } else {
            val c = level[newPosition.y][newPosition.x]
            if (c == '#' || newPosition == positionToBlock) {
                direction = (direction + 1) % 4
            } else {
                position = newPosition
                val newState = Pair(direction, position)
                if (previousStates.contains(newState)) {
                    return true
                }
                previousStates += newState
            }
        }
    }
    return false
}

private fun blockGuard(level: List<String>, maxX: Int, maxY: Int, startingPosition: Vec2i, uniquePositions: Set<Vec2i>): Int {
    return uniquePositions
        .filter { it != startingPosition }
        .filter { simulateLooping(level, maxX, maxY, startingPosition, it) }
        .size
}
