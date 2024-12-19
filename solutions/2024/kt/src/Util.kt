import java.io.File

typealias Solution = Pair<String, String>

fun Solution(a: Int, b: Int): Solution = Pair(a.toString(), b.toString())

fun Solution(a: Long, b: Long): Solution = Pair(a.toString(), b.toString())

fun getInput(day: Int): List<String> {
    return File("../../../data/2024/day${day}.txt").readLines()
}

fun getOutput(day: Int): Solution {
    return File("../../../data/2024/output${day}.txt").readText().split(" ").toPair()
}

fun writeOutput(day: Int, value: Solution) {
    return File("../../../data/2024/output${day}.txt").writeText("${value.first} ${value.second}")
}

fun List<String>.mapToInt(): List<Int> {
    return map { it.toInt() }
}

fun List<String>.mapToLong(): List<Long> {
    return map { it.toLong() }
}

fun <T> List<T>.toPair(): Pair<T, T> {
    return Pair(this[0], this[1])
}
