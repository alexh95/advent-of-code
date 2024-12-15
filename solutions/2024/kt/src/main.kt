const val daysGone = 14

fun main() {
    val day = daysGone
    val solution = solve(day)
    println(solution)
    writeOutput(day, solution)
}

fun solve(day: Int): Solution = solve(day, getInput(day))

fun solve(day: Int, input: List<String>): Solution {
    return when (day) {
        1 -> solveDay1(input)
        2 -> solveDay2(input)
        3 -> solveDay3(input)
        4 -> solveDay4(input)
        5 -> solveDay5(input)
        6 -> solveDay6(input)
        7 -> solveDay7(input)
        8 -> solveDay8(input)
        9 -> solveDay9(input)
        10 -> solveDay10(input)
        11 -> solveDay11(input)
        12 -> solveDay12(input)
        13 -> solveDay13(input)
        14 -> solveDay14(input)
        else -> Pair(0, 0)
    }
}
