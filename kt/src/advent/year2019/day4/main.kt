package advent.year2019.day4

fun validPassword(n: Int): Boolean {
    val digits = n.toString().map { it.toInt() }
    if (digits[0] <= digits[1] && digits[1] <= digits[2] && digits[2] <= digits[3] && digits[3] <= digits[4] && digits[4] <= digits[5]) {
        if (digits[0] == digits[1] || digits[1] == digits[2] || digits[2] == digits[3] || digits[3] == digits[4] || digits[4] == digits[5]) {
            return true
        }
    }
    return false
}

fun main() {
    val min: Int = 256310
    val max: Int = 732736
    val passwordCount = (min..max).filter { validPassword(it) }.size
    println("$passwordCount\n")
}
