package advent.year2019.day4

fun List<Int>.isSorted() = zip(drop(1)).all { (a, b) -> a <= b}

fun validPassword(n: Int): Boolean {
    val digits = n.toString().map { it.toInt() - 48 }
    if (digits.isSorted()) {
        val digitCount: IntArray = IntArray(10) { 0 }
        digits.forEach { ++digitCount[it] }
        return digitCount.any { it > 1 }
    }
    return false
}

fun validPasswordMinDouble(n: Int): Boolean {
    val digits = n.toString().map { it.toInt() - 48 }
    if (digits.isSorted()) {
        val digitCount: IntArray = IntArray(10) { 0 }
        digits.forEach { ++digitCount[it] }
        return digitCount.contains(2)
    }
    return false
}

fun main() {
    val min: Int = 256310
    val max: Int = 732736
    val passwordCount: Int = (min..max).filter { validPassword(it) }.size
    val passwordCountMinDouble: Int = (min..max).filter { validPasswordMinDouble(it) }.size
    println("$passwordCount\n$passwordCountMinDouble\n")
}
