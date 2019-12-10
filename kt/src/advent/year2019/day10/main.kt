package advent.year2019.day10

import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.test.assertEquals

fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)

fun gcd(a: V2i): Int = gcd(a.x, a.y)

class V2i(val x: Int, val y: Int) {
    operator fun plus(a: V2i): V2i = V2i(x + a.x, y + a.y)
    operator fun minus(a: V2i): V2i = V2i(x - a.x, y - a.y)
    operator fun div(n: Int): V2i = V2i(x / n, y / n)
    fun minimize(): V2i = this / max(abs(gcd(this)), 1)
    override fun equals(other: Any?): Boolean = x == (other as V2i).x && y == other.y
    override fun hashCode(): Int = x * 31 + y * 7
}

typealias Asteroids = List<V2i>

fun List<Pair<V2i, Int>>.max() = reduceRight { pair, acc -> if (pair.second > acc.second) pair else acc }

fun readAsteroids(lines: List<String>): Asteroids {
    val grid = lines.map { it.toList() }
    val rows = grid.size
    val cols = grid.first().size
    return grid.flatten().mapIndexed { index, c -> Pair(index, c) }.filter { it.second == '#' }.map { V2i(it.first % cols, it.first / rows) }
}

fun stationPositionAsteroids(asteroids: Asteroids): Pair<V2i, Int> = asteroids.map { position -> Pair(position, asteroids.filterNot { it == position }.map { (it - position).minimize() }.toSet().size) }.max()

// up -> right -> down -> left: -0.5PI .. PI -> 0 .. 1.5 PI
// left -> up: -PI .. -0.5 PI -> 1.5 PI .. 2 PI
fun mapAngle(a: Double): Double = if (a >= -0.5 * PI && a <= PI) a + 0.5 * PI else a + 2.5 * PI

fun destroyAsteroids(asteroids: Asteroids, stationPosition: V2i): List<V2i> = asteroids.filterNot { it == stationPosition }.map { it - stationPosition }.map { Pair(it.minimize(), it) }.groupBy { it.first }.mapValues { it.value.map { p -> p.second }.sortedBy { v -> abs(v.x) + abs(v.y) } }.flatMap { it.value.mapIndexed { index, p -> Pair(it.key, Pair(index, p)) } }.sortedBy { it.second.first * 1000 + mapAngle(atan2(it.first.y.toDouble(), it.first.x.toDouble())) }.map { it.second.second + stationPosition }

fun main() {
    val testInput00 = ".#..#\n" + ".....\n" + "#####\n" + "....#\n" + "...##"
    val stationPosition00 = stationPositionAsteroids(readAsteroids(testInput00.split("\n")))
    assertEquals(8, stationPosition00.second)

    val asteroids = readAsteroids(File("src/advent/year2019/day10/input.txt").readLines())
    val stationPosition = stationPositionAsteroids(asteroids)
    val asteroidsDestroyed = destroyAsteroids(asteroids, stationPosition.first)
    val position = asteroidsDestroyed[199]
    println("${stationPosition.second}\n${position.x * 100 + position.y}\n")
}
