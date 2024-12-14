import kotlin.math.min

fun solveDay13(input: List<String>): Solution {
    val values = input.joinToString("\n").split("\n\n")
        .map { group -> group.split("\n").map { extractButtonCoordinates(it) } }

    val minTokens = findAllMinTokenForMaxW(values)
    val minTokensStupidCase = findAllStupidMinTokens(values)
    return Solution(minTokens, minTokensStupidCase)
}

private fun findAllMinTokenForMaxW(values: List<List<List<Long>>>): Long {
    return values.sumOf { minTokenForW(it[0][0], it[0][1], it[1][0], it[1][1], it[2][0], it[2][1]) }
}

private const val WTF = 10000000000000L

private fun findAllStupidMinTokens(values: List<List<List<Long>>>): Long {
    return values.sumOf { minTokenForW(it[0][0], it[0][1], it[1][0], it[1][1], it[2][0] + WTF, it[2][1] + WTF) }
}

private fun minTokenForW(ax: Long, ay: Long, bx: Long, by: Long, goalX: Long, goalY: Long): Long {
    val initialMaxFactorB = min(goalX / bx, goalY / by)
    val initialFactorAX = (goalX - bx * initialMaxFactorB) / ax
    val initialFactorAY = (goalY - by * initialMaxFactorB) / ay
    val initialRatioFavorsX = if (initialFactorAX > initialFactorAY) 1 else -1
    var maxFactorB = initialMaxFactorB
    var minFactorB = 0L
    while (minFactorB <= maxFactorB) {
        val factorB = minFactorB + (maxFactorB - minFactorB) / 2
        val remainderX = goalX - bx * factorB
        val remainderY = goalY - by * factorB
        val factorAX = remainderX / ax
        val factorAY = remainderY / ay
        val ratioFavorsX = if (factorAX == factorAY) {
            val remainderAX = remainderX - factorAX * ax
            val remainderAY = remainderY - factorAY * ay
            if (remainderAX == 0L && remainderAY == 0L) {
                return 3L * factorAX + factorB
            } else {
                if (remainderAX > remainderAY) 1 else -1
            }
        } else {
            if (factorAX > factorAY) 1 else -1
        }
        if (initialRatioFavorsX == ratioFavorsX) {
            maxFactorB = factorB - 1
        } else {
            minFactorB = factorB + 1
        }
    }
    return 0
}

private fun extractButtonCoordinates(line: String): List<Long> {
    return Regex(".+X.(\\d+), Y.(\\d+)").find(line)!!.groups.drop(1).map { it!!.value.toLong() }
}
