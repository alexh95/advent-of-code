package advent.year2019.day8

import java.io.File

fun main() {
    val rowSize = 25
    val colSize = 6
    val imageSize = rowSize * colSize
    val layers: List<String> = File("src/advent/year2019/day8/input.txt").readText().trim().chunked(imageSize)
    val min0Layer: String = layers.minBy { it.count {c -> c == '0'} }!!
    val count1 = min0Layer.count { it == '1' }
    val count2 = min0Layer.count { it == '2' }
    val value = count1 * count2
    println("$value\n")
}
