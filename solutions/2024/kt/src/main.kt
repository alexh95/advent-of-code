val daysGone = 2

fun main() {
    val solution = solveDay2(getInput(2))
    println(solution)
}

fun solve(day: Int, input: List<String>): Pair<Int, Int> {
    return when (day) {
        1 -> solveDay1(input)
        2 -> solveDay2(input)
        else -> Pair(0, 0)
    }
}
