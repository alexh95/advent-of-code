package advent.year2019.day8

import java.io.File

fun String.cover(s: String): String = zip(s).map { if (it.first == '2') it.second else it.first }.joinToString("")

fun main() {
    val rowSize = 25
    val colSize = 6
    val imageSize = rowSize * colSize

    val layers: List<String> = File("src/advent/year2019/day8/input.txt").readText().trim().chunked(imageSize)
    val min0Layer: String = layers.minBy { it.count {c -> c == '0'} }!!
    val value = min0Layer.count { it == '1' } * min0Layer.count { it == '2' }

    val image = layers.reduceRight { s, acc -> s.cover(acc) }
    val displayImage = image.chunked(rowSize).joinToString("\n")

    println("$value\n$displayImage\n")
}
