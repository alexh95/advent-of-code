import kotlin.math.max

fun solveDay5(input: List<String>): Pair<Int, Int> {
    val separatorIndex = input.indexOf("")
    val rules = input.take(separatorIndex)
        .map { it.split("|") }
        .map { it.mapToInt() }
        .map { it.toPair() }
    val maxPage = max(rules.maxOf { it.first }, rules.maxOf { it.second })
    val precedenceTable = (0 .. maxPage + 1).map { IntArray(maxPage + 1) }
    rules.forEach {
        precedenceTable[it.first][it.second] = 1
        precedenceTable[it.second][it.first] = -1
    }

    val updates = input.drop(separatorIndex + 1)
        .map { it.split(",") }
        .map { it.mapToInt() }
    val validUpdateSum = validateUpdates(precedenceTable, updates)
    val correctedUpdateSum = correctUpdates(precedenceTable, updates)
    return Pair(validUpdateSum, correctedUpdateSum)
}

private fun validateUpdates(precedenceTable: List<IntArray>, updates: List<List<Int>>): Int {
    return updates
        .filter { isUpdateValid(precedenceTable, it) }
        .sumOf { it[it.size / 2] }
}

private fun correctUpdates(precedenceTable: List<IntArray>, updates: List<List<Int>>): Int {
    return updates
        .filterNot { isUpdateValid(precedenceTable, it) }
        .map { correctUpdate(precedenceTable, it) }
        .sumOf { it[it.size / 2] }
}

private fun isUpdateValid(precedenceTable: List<IntArray>, update: List<Int>): Boolean {
    return update.zip(update.drop(1))
        .all { (a, b) -> precedenceTable[a][b] > 0 }
}

private fun correctUpdate(precedenceTable: List<IntArray>, update: List<Int>): List<Int> {
    return update
        .sortedWith { a, b -> precedenceTable[a][b] }
}
