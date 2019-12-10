package advent.year2019.day10

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)

fun gcd(a: V2i): Int = gcd(a.x, a.y)

class V2i(val x: Int, val y: Int) {
    operator fun minus(a: V2i): V2i = V2i(x - a.x, y - a.y)
    operator fun div(n: Int): V2i = V2i(x / n, y / n)
    override fun equals(other: Any?): Boolean = x == (other as V2i).x && y == (other as V2i).y
    override fun hashCode(): Int = x * 31 + y * 7
}

fun maxAsteroids(lines: List<String>): Int {
    val grid = lines.map { it.toList() }
    val rows = grid.size
    val cols = grid.first().size
    val asteroids = grid.flatten().mapIndexed { index, c -> Pair(index, c) }.filter { it.second == '#' }.map { V2i(it.first % cols, it.first / rows) }
    return asteroids.map { position ->
        val asteroidAngleMap: MutableMap<V2i, V2i> = mutableMapOf()
        asteroids.forEach {
            val d = it - position
            if (d.x != 0 || d.y != 0) {
                val rd = d / max(abs(gcd(d)), 1)
                asteroidAngleMap[rd] = it
            }
        }
        asteroidAngleMap.keys.size
    }.max()!!
}

fun main() {
    val testInput00 = ".#..#\n" + ".....\n" + "#####\n" + "....#\n" + "...##"
    val max00 = maxAsteroids(testInput00.split("\n"))
    assertEquals(8, max00)

    val max0 = maxAsteroids(File("src/advent/year2019/day10/input.txt").readLines())
    println("$max0\n")
}
