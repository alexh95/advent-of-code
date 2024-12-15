import kotlin.concurrent.thread

fun main() {
    for (day in (1..daysGone)) {
        thread { runTest(day) }
    }
}

private fun runTest(day: Int) {
    val result = StringBuilder("Testing day $day ")
    val timeBefore = System.currentTimeMillis()
    if (testSolver(day)) {
        result.append("✅")
    } else {
        result.append("❌")
    }
    val timeAfter = System.currentTimeMillis()
    result.append(" in ${timeAfter - timeBefore}ms")
    println(result)
}

private fun testSolver(day: Int): Boolean {
    val input = getInput(day)
    val output = getOutput(day)
    val solution = solve(day, input)
    return output == solution
}
