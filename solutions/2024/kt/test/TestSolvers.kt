fun main() {
    var notFailed = true
    for (day in (1..daysGone)) {
        print("Testing day $day ")
        val timeBefore = System.currentTimeMillis()
        if (testSolver(day)) {
            print("✅")
        } else {
            print("❌")
            notFailed = false
        }
        val timeAfter = System.currentTimeMillis()
        println(" in ${timeAfter - timeBefore}ms")
    }
    if (notFailed) {
        println("All tests passed successfully")
    } else {
        throw Error("Not all solvers passed successfully.")
    }
}

private fun testSolver(day: Int): Boolean {
    val input = getInput(day)
    val output = getOutput(day)
    val solution = solve(day, input)
    return output == solution
}
