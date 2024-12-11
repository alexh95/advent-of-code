fun solveDay8(input: List<String>): Solution {
    val antennaToPositions = input.flatMapIndexed { r, line ->
        line.mapIndexed { c, char -> Pair(char, Vec2i(c, r)) }
            .filter { it.first != '.' }
    }.groupBy({ (char, _) -> char }, { (_, pos) -> pos } )
    val maxY = input.size
    val maxX = input[0].length
    val uniqueAntinodes = countAntinodes(antennaToPositions, maxX, maxY)
    val uniqueAllAntinodes = countAllAntinodes(antennaToPositions, maxX, maxY)
    return Solution(uniqueAntinodes, uniqueAllAntinodes)
}

private fun countAntinodes(antennaToPositions: Map<Char, List<Vec2i>>, maxX: Int, maxY: Int): Int {
    return antennaToPositions.values
        .flatMap { eachPair(it).flatMap { pair -> antinodePositions(pair) } }
        .filter { it.x in 0 until maxX && it.y in 0 until maxY }
        .distinct().size
}

private fun countAllAntinodes(antennaToPositions: Map<Char, List<Vec2i>>, maxX: Int, maxY: Int): Int {
    return antennaToPositions.values
        .flatMap { eachPair(it).flatMap { pair -> fullAntinodePositions(pair, maxX, maxY) } }
        .distinct().size
}

private fun antinodePositions(pair: Pair<Vec2i, Vec2i>): List<Vec2i> {
    val delta = pair.second - pair.first
    return listOf(pair.first - delta, pair.second + delta)
}

private fun fullAntinodePositions(pair: Pair<Vec2i, Vec2i>, maxX: Int, maxY: Int): List<Vec2i> {
    val result = mutableListOf<Vec2i>()
    val delta = pair.second - pair.first
    var pos = pair.first
    while (pos.x in 0 until maxX && pos.y in 0 until maxY) {
        result += pos
        pos -= delta
    }
    pos = pair.second
    while (pos.x in 0 until maxX && pos.y in 0 until maxY) {
        result += pos
        pos += delta
    }
    return result
}

private fun eachPair(antennae: List<Vec2i>): List<Pair<Vec2i, Vec2i>> {
    val result = mutableListOf<Pair<Vec2i, Vec2i>>()
    for (i in 0 until antennae.size - 1) {
        for (j in i + 1 until antennae.size) {
            result += Pair(antennae[i], antennae[j])
        }
    }
    return result
}
