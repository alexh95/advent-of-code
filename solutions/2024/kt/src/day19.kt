fun solveDay19(input: List<String>): Solution {
    val towels = input[0].split(", ")
    val patterns = input.drop(2)
    val patternSolutionsCount = countPossiblePatterns(towels, patterns)
    val possiblePatternCount = patternSolutionsCount.count { it > 0 }.toLong()
    val allPossiblePatternCount = patternSolutionsCount.sum()
    return Solution(possiblePatternCount, allPossiblePatternCount)
}

private fun countPossiblePatterns(towels: List<String>, patterns: List<String>): List<Long> {
    return patterns.map { countPossiblePatterns(towels, it) }
}

private fun countPossiblePatterns(towels: List<String>, pattern: String): Long {
    val indexToCount = mutableMapOf<Int, Long>()
    val patternOffsetToAvailableTowels = pattern.indices.map { pattern.substring(it) }
        .map { subPattern -> towels.filter { towel -> subPattern.indexOf(towel) == 0 } }
    return findMatchingTowel(indexToCount, patternOffsetToAvailableTowels, pattern, 0)
}

private fun findMatchingTowel(indexToCount: MutableMap<Int, Long>, patternOffsetToAvailableTowels: List<List<String>>, pattern: String, patternOffset: Int): Long {
    if (indexToCount.contains(patternOffset)) {
        return indexToCount[patternOffset]!!
    }

    var combinations = 0L
    val usefulTowels = patternOffsetToAvailableTowels[patternOffset]
    for (towel in usefulTowels) {
        if (pattern.indexOf(towel, patternOffset) == patternOffset) {
            if (towel.length == pattern.length - patternOffset) {
                combinations += 1
            } else {
                combinations += findMatchingTowel(indexToCount, patternOffsetToAvailableTowels, pattern, patternOffset + towel.length)
            }
        }
    }
    indexToCount[patternOffset] = combinations

    return combinations
}
