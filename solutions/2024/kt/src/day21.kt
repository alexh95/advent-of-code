import kotlin.math.abs

fun solveDay21(input: List<String>): Solution {
    val codeComplexity = calculateCodeComplexity(input, 2)
    val bigCodeComplexity = calculateCodeComplexity(input, 25)
    return Solution(codeComplexity, bigCodeComplexity)
}

private fun calculateCodeComplexity(input: List<String>, times: Int): Long {
    val numericPart = input.map { it.substring(0, 3).toLong() }
    val codeLengths = input.map { keypadCodeLength(it, times) }
    return numericPart.zip(codeLengths).sumOf { (number, length) -> number * length }
}

private fun keypadCodeLength(code: String, times: Int): Long {
    val firstDirectionalCode = pressOnSpecificKeypad(numericKeyToShortestPath, code)
    val cache = mutableMapOf<Pair<String, Int>, Long>()
    val finalCodeLength = pressR(cache, firstDirectionalCode, 1, times)
    return finalCodeLength
}

private fun pressR(cache: MutableMap<Pair<String, Int>, Long>, code: String, depth: Int, maxDepth: Int): Long {
    if (cache.contains(Pair(code, depth))) {
        return cache[Pair(code, depth)]!!
    }

    var result = 0L
    for (pair in "A$code".zip(code)) {
        val newCode = directionalKeyToShortestPath[pair]!!
        if (depth < maxDepth) {
            result += pressR(cache, newCode, depth + 1, maxDepth)
        } else {
            result += newCode.length
        }
    }

    cache[Pair(code, depth)] = result
    return result
}

private fun pressOnSpecificKeypad(keyPairToDirectionalCode: Map<Pair<Char, Char>, String>, code: String): String {
    val charPairs = ("A$code").zip(code)
    return charPairs.joinToString("") { keyPairToDirectionalCode[it]!! }
}

private fun shortestPath(keyToPos: Map<Char, Vec2i>, charFrom: Char, charTo: Char): String {
    val result = StringBuilder()
    val posFrom = keyToPos[charFrom]!!
    val posTo = keyToPos[charTo]!!
    val diff = posTo - posFrom
    val gapPos = keyToPos[' ']!!
    val mustStartX = posFrom.x == gapPos.x && posTo.y == gapPos.y
    val mustStartY = posTo.x == gapPos.x && posFrom.y == gapPos.y
    val preferEndX = diff.x > 0

    if (mustStartX || (!mustStartY && !preferEndX)) {
        repeat(abs(diff.x)) {
            result.append(if (diff.x < 0) '<' else '>')
        }
        repeat(abs(diff.y)) {
            result.append(if (diff.y < 0) '^' else 'v')
        }
    } else {
        repeat(abs(diff.y)) {
            result.append(if (diff.y < 0) '^' else 'v')
        }
        repeat(abs(diff.x)) {
            result.append(if (diff.x < 0) '<' else '>')
        }
    }
    result.append('A')
    return result.toString()
}

private val numericKeys = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A')

private val numericKeypad = listOf(
    "789",
    "456",
    "123",
    " 0A",
)

private val numericKeyToPos = numericKeypad
    .flatMapIndexed { r, line -> line.mapIndexed { c, char -> Pair(char, Vec2i(c, r)) } }.toMap()

private val numericKeyToShortestPath = numericKeys
    .flatMap { keyFrom -> numericKeys.map { keyTo -> Pair(keyFrom, keyTo) } }
    .associate { (keyFrom, keyTo) -> Pair(Pair(keyFrom, keyTo), shortestPath(numericKeyToPos, keyFrom, keyTo)) }

private val directionalKeys = listOf('^', '<', 'v', '>', 'A')

private val directionalKeypad = listOf(
    " ^A",
    "<v>",
)

private val directionalKeyToPos = directionalKeypad
    .flatMapIndexed { r, line -> line.mapIndexed { c, char -> Pair(char, Vec2i(c, r)) } }.toMap()

private val directionalKeyToShortestPath = directionalKeys
    .flatMap { keyFrom -> directionalKeys.map { keyTo -> Pair(keyFrom, keyTo) } }
    .associate { (keyFrom, keyTo) -> Pair(Pair(keyFrom, keyTo), shortestPath(directionalKeyToPos, keyFrom, keyTo)) }
