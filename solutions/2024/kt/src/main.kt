const val daysGone = 4

fun main() {
    val day = daysGone
    val solution = solve(day)
    println(solution)
    writeOutput(day, solution)
}

fun solve(day: Int): Pair<Int, Int> = solve(day, getInput(day))

fun solve(day: Int, input: List<String>): Pair<Int, Int> {
    return when (day) {
        1 -> solveDay1(input)
        2 -> solveDay2(input)
        3 -> solveDay3(input)
        4 -> solveDay4(input)
        else -> Pair(0, 0)
    }
}
