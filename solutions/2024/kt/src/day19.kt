fun solveDay19(input: List<String>): Solution {
//    val input = ("r, wr, b, g, bwu, rb, gb, br\n" +
//            "\n" +
//            "brwrr\n" +
//            "bggr\n" +
//            "gbbr\n" +
//            "rrbgbr\n" +
//            "ubwu\n" +
//            "bwurrg\n" +
//            "brgr\n" +
//            "bbrgwb").split("\n")
    val towels = input[0].split(", ")
    val patterns = input.drop(2)
    val possiblePatternCount = countPossiblePatterns(towels, patterns)
    val allPossiblePatternCount = countAllPossiblePatterns(towels, patterns)
    return Solution(possiblePatternCount, allPossiblePatternCount)
}

private fun countPossiblePatterns(towels: List<String>, patterns: List<String>): Int {
    return patterns.count { isPossiblePattern(towels, it) }
}

private fun countAllPossiblePatterns(towels: List<String>, patterns: List<String>): Int {
    return patterns.sumOf { countPossiblePatterns(towels, it) }
}

private fun isPossiblePattern(towels: List<String>, pattern: String): Boolean {
    return countPossiblePatterns(towels, pattern) > 0
}

private fun countPossiblePatterns(towels: List<String>, pattern: String): Int {
    val patternOffsetToAvailableTowels = pattern.indices.map { pattern.substring(it) }
        .map { subPattern -> towels.filter { towel -> subPattern.indexOf(towel) == 0 } }
    return findMatchingTowel(patternOffsetToAvailableTowels, pattern, 0)
}

private fun findMatchingTowel(patternOffsetToAvailableTowels: List<List<String>>, pattern: String, patternOffset: Int): Int {
    var combinations = 0
    val usefulTowels = patternOffsetToAvailableTowels[patternOffset]
    for (towel in usefulTowels) {
        if (pattern.indexOf(towel, patternOffset) == patternOffset) {
            if (towel.length == pattern.length - patternOffset) {
                combinations += 1
            } else {
                combinations += findMatchingTowel(patternOffsetToAvailableTowels, pattern, patternOffset + towel.length)
            }
        }
    }
    return combinations
}
