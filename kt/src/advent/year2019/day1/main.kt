package advent.year2019.day1

import java.io.File

fun main() {
    val fuelSum = File("src/advent/year2019/day1/input.txt").readLines().map{it.toInt() / 3 - 2}.sum()
    println(fuelSum)
}
