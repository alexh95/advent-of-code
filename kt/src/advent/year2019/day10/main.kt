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
    override fun equals(other: Any?): Boolean = x == (other as V2i).x && y == (other as V2i).y
    override fun hashCode(): Int = x * 31 + y * 7
}

typealias Asteroids = Pair<List<List<Char>>, List<V2i>>

val Asteroids.grid get() = first

val Asteroids.positions get() = second

fun List<Pair<V2i, Int>>.max() = reduceRight { pair, acc -> if (pair.second > acc.second) pair else acc }

fun readAsteroids(lines: List<String>): Asteroids {
    val grid = lines.map { it.toList() }
    val rows = grid.size
    val cols = grid.first().size
    val positions = grid.flatten().mapIndexed { index, c -> Pair(index, c) }.filter { it.second == '#' }.map { V2i(it.first % cols, it.first / rows) }
    return Asteroids(grid, positions)
}

fun stationPositionAsteroids(asteroids: Asteroids): Pair<V2i, Int> {
    return asteroids.positions.map { position ->
        val asteroidAngleMap: MutableMap<V2i, V2i> = mutableMapOf()
        asteroids.positions.forEach {
            val d = it - position
            if (d.x != 0 || d.y != 0) {
                val rd = d / max(abs(gcd(d)), 1)
                asteroidAngleMap[rd] = it
            }
        }
        Pair(position, asteroidAngleMap.keys.size)
    }.max()
}

// up -> right -> down -> left: -0.5PI .. PI -> 0 .. 1.5 PI
// left -> up: -PI .. -0.5 PI -> 1.5 PI .. 2 PI
fun mapAngle(a: Double): Double = if (a >= -0.5 * PI && a <= PI) a + 0.5 * PI else a + 2.5 * PI

fun destroyAsteroids(asteroids: Asteroids, stationPosition: V2i): List<V2i> {
    val result: MutableList<V2i> = mutableListOf()
    val asteroidAngleMap: MutableMap<V2i, MutableList<V2i>> = mutableMapOf()
    asteroids.positions.filterNot { it == stationPosition }.forEach {
        val d = it - stationPosition
        val rd = d / max(abs(gcd(d)), 1)
        if (asteroidAngleMap.containsKey(rd)) {
            asteroidAngleMap[rd]!! += d
        } else {
            asteroidAngleMap[rd] = mutableListOf(d)
        }
    }
    asteroidAngleMap.forEach { (_, positions) -> positions.sortBy {abs(it.x) + abs(it.y)} }
    val angles = asteroidAngleMap.keys.sortedBy { mapAngle(atan2(it.y.toDouble(), it.x.toDouble())) }.toMutableList()
    while (angles.isNotEmpty()) {
        val toBeRemoved: MutableList<V2i> = mutableListOf()
        angles.forEach {
            val positions = asteroidAngleMap[it]!!
            val pos = positions.removeAt(0)
            result += pos + stationPosition
            if (positions.isEmpty()) {
                toBeRemoved += it
            }
        }
        angles.removeAll(toBeRemoved)
    }
    return result
}

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
