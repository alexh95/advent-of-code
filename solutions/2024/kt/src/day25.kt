fun solveDay25(input: List<String>): Solution {
    val patterns = input.joinToString("\n").split("\n\n").map { it.split("\n") }
    val locks = patterns
        .filter { it[0][0] == '#' }
        .map { it.drop(1).dropLast(1) }
        .map { parsePattern(it) }
    val keys = patterns
        .filter { it[0][0] == '.' }
        .map { it.drop(1).dropLast(1) }
        .map { parsePattern(it) }
    val count = fitCount(locks, keys)
    return Solution(count, 0)
}

private fun fitCount(locks: List<IntArray>, keys: List<IntArray>): Int {
    var result = 0
    for (lock in locks) {
        for (key in keys) {
            if (keyFitsInLock(lock, key)) {
                result++
            }
        }
    }
    return result
}

private fun keyFitsInLock(lock: IntArray, key: IntArray): Boolean {
    return lock.zip(key).map { it.first + it.second }.none { it > 5 }
}

private fun parsePattern(pattern: List<String>): IntArray {
    val result = IntArray(pattern[0].length)
    for (line in pattern) {
        for ((index, character) in line.withIndex()) {
            if (character == '#') {
                result[index]++
            }
        }
    }
    return result
}
