fun solveDay15(input: List<String>): Solution {
    val separatorIndex = input.indexOf("")
    val level = input.take(separatorIndex)
    val instructions = input.drop(separatorIndex + 1).flatMap { line -> line.map { parseDirection(it) } }
    val boxScore = simulateAndComputeBoxScore(level, instructions)
    val bigBoxScore = simulateBiglyAndComputeBigBoxScore(level, instructions)
    return Solution(boxScore, bigBoxScore)
}

private fun simulateAndComputeBoxScore(initialLevel: List<String>, instructions: List<Vec2i>): Int {
    val level = initialLevel.map { it.toCharArray() }
    val y = level.indexOfFirst { it.contains('@') }
    val x = level[y].indexOf('@')
    var position = Vec2i(x, y)
    for (instruction in instructions) {
       position = move(level, position, instruction, '@')
    }
    return boxScore(level)
}

private fun simulateBiglyAndComputeBigBoxScore(initialLevel: List<String>, instructions: List<Vec2i>): Int {
    val level = initialLevel.map { line -> line.flatMap {
        when (it) {
            '#' -> listOf('#', '#')
            '.' -> listOf('.', '.')
            'O' -> listOf('[', ']')
            '@' -> listOf('@', '.')
            else -> throw Error("BAD!")
        }
    }.toCharArray() }
    val y = level.indexOfFirst { it.contains('@') }
    val x = level[y].indexOf('@')
    var position = Vec2i(x, y)
    for (instruction in instructions) {
        position = move(level, position, instruction, '@')
    }
    return bigBoxScore(level)
}

private fun boxScore(level: List<CharArray>): Int {
    return calculateBoxScore(level, 'O')
}

private fun bigBoxScore(level: List<CharArray>): Int {
    return calculateBoxScore(level, '[')
}

private fun calculateBoxScore(level: List<CharArray>, boxChar: Char): Int {
    return level.flatMapIndexed { y, row ->
        row.mapIndexed { x, char -> Pair(Vec2i(x, y), char) }
    }.filter { it.second == boxChar }
        .sumOf { (pos, _) -> 100 * pos.y + pos.x }
}

private fun move(level: List<CharArray>, position: Vec2i, direction: Vec2i, char: Char): Vec2i {
    val newPosition = position + direction
    val charAtNew = level[newPosition.y][newPosition.x]
    if (charAtNew == '#') {
        return position
    } else if (charAtNew == '.') {
        level[position.y][position.x] = '.'
        level[newPosition.y][newPosition.x] = char
        return newPosition
    } else if ((charAtNew == 'O') || (direction.y == 0 && (charAtNew == '[' || charAtNew == ']'))) {
        val newBoxPosition = move(level, newPosition, direction, charAtNew)
        if (newBoxPosition == newPosition) {
            return position
        }
        move(level, position, direction, char)
        return newPosition
    } else if (charAtNew == '[' || charAtNew == ']') {
        val savedState = level.map { line -> line.toList() }
        val boxInitialLeftPosition = if (charAtNew == '[') newPosition else newPosition + LEFT
        val boxInitialRightPosition = boxInitialLeftPosition + RIGHT
        val boxLeftPosition = move(level, boxInitialLeftPosition, direction, '[')
        if (boxInitialLeftPosition == boxLeftPosition) {
            restoreLevel(level, savedState)
            return position
        }
        val boxRightPosition = move(level, boxInitialRightPosition, direction, ']')
        if (boxInitialRightPosition == boxRightPosition) {
            restoreLevel(level, savedState)
            return position
        }
        move(level, position, direction, char)
        return newPosition
    } else {
        throw Error("HUH!?")
    }
}

private fun restoreLevel(
    level: List<CharArray>,
    savedState: List<List<Char>>
) {
    level.zip(savedState).forEach { (line, savedLine) ->
        for ((index, c) in savedLine.withIndex()) {
            line[index] = c
        }
    }
}

private val RIGHT = Vec2i(1, 0)
private val UP = Vec2i(0, -1)
private val LEFT = Vec2i(-1, 0)
private val DOWN = Vec2i(0, 1)

private fun parseDirection(char: Char): Vec2i {
    return when (char) {
        '>' -> RIGHT
        '^' -> UP
        '<' -> LEFT
        'v' -> DOWN
        else -> throw Error("Wrong direction character")
    }
}
