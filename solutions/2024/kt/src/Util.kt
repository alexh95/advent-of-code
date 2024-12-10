import java.io.File

fun getInput(day: Int): List<String> {
    return File("../../../data/2024/day${day}.txt").readLines()
}

fun getOutput(day: Int): Pair<Long, Long> {
    return File("../../../data/2024/output${day}.txt").readText().split(" ").map { it.toLong() }.toPair()
}

fun writeOutput(day: Int, value: Pair<Number, Number>) {
    return File("../../../data/2024/output${day}.txt").writeText("${value.first} ${value.second}")
}

fun List<String>.mapToInt(): List<Int> {
    return map { it.toInt() }
}

fun <T> List<T>.toPair(): Pair<T, T> {
    return Pair(this[0], this[1])
}
