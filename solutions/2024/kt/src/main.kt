import java.io.File

fun main() {
    val solution = solveDay2(getInput(2))
    println(solution)
}

private fun getInput(day: Int): List<String> {
    return File("../../../data/2024/day${day}.txt").readLines()
}
