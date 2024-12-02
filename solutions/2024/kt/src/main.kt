import java.io.File

fun main() {
    val solution = solveDay1(getInput(1))
    println(solution)
}

private fun getInput(day: Int): List<String> {
    return File("../../../data/2024/day${day}.txt").readLines()
}
