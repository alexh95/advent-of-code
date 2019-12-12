package advent.year2019.day12

import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals

data class V3i(val x: Int, val y: Int, val z: Int) {
    constructor(array: IntArray) : this(array[0], array[1], array[2])
    constructor() : this(0, 0, 0)

    operator fun plus(v: V3i): V3i = V3i(x + v.x, y + v.y, z + v.z)
    fun absum(): Int = abs(x) + abs(y) + abs(z)
    override fun toString(): String = "<x=$x, y=$y, z=$z>"
}

fun List<V3i>.sum(): V3i = reduceRight { v, acc -> v + acc }

data class Moon(val p: V3i, val dp: V3i) {
    constructor(p: V3i) : this(p, V3i())

    fun update(moons: List<Moon>): Moon {
        val newDP = dp + moons.map { m -> V3i(sign(m.p.x - p.x), sign(m.p.y - p.y), sign(m.p.z - p.z)) }.sum()
        val newP = p + newDP
        return Moon(newP, newDP)
    }

    override fun toString(): String = "p=$p, dp=$dp"
}

fun sign(n: Int): Int = if (n > 0) 1 else if (n == 0) 0 else -1

fun getMoons(input: List<String>): List<Moon> = input.map { V3i(it.substring(1, it.length - 1).split(", ").map { s -> s.substringAfter('=').toInt() }.toIntArray()) }.map { Moon(it) }

fun totalEnergy(input: List<String>, steps: Int): Int {
    var moons = getMoons(input)
    repeat(steps) { moons = moons.map { it.update(moons) } }
    return moons.map { it.p.absum() * it.dp.absum() }.sum()
}

fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)

fun lcm(x: Long, y: Long): Long = x * y / gcd(x, y)

fun minStepsToPrevious(input: List<String>): Long {
    var moons = getMoons(input)
    val initialXValues = moons.map { listOf(it.p.x, it.dp.x) }
    val initialYValues = moons.map { listOf(it.p.y, it.dp.y) }
    val initialZValues = moons.map { listOf(it.p.z, it.dp.z) }
    var xPeriod = -1L
    var yPeriod = -1L
    var zPeriod = -1L
    var step = 0L
    while (xPeriod == -1L || yPeriod == -1L || zPeriod == -1L) {
        moons = moons.map { it.update(moons) }
        ++step

        if (xPeriod == -1L && initialXValues == moons.map { listOf(it.p.x, it.dp.x) }) {
            xPeriod = step
        }
        if (yPeriod == -1L && initialYValues == moons.map { listOf(it.p.y, it.dp.y) }) {
            yPeriod = step
        }
        if (zPeriod == -1L && initialZValues == moons.map { listOf(it.p.z, it.dp.z) }) {
            zPeriod = step
        }
    }
    return lcm(xPeriod, lcm(yPeriod, zPeriod))
}

fun main() {
    val inputTest0 = listOf("<x=-1, y=0, z=2>", "<x=2, y=-10, z=-7>", "<x=4, y=-8, z=8>", "<x=3, y=5, z=-1>")
    val energyTest0 = totalEnergy(inputTest0, 10)
    val minStepsTest0 = minStepsToPrevious(inputTest0)
    assertEquals(179, energyTest0)
    assertEquals(2772L, minStepsTest0)

    val inputTest1 = listOf("<x=-8, y=-10, z=0>", "<x=5, y=5, z=10>", "<x=2, y=-7, z=3>", "<x=9, y=-8, z=-3>")
    val energyTest1 = totalEnergy(inputTest1, 100)
    val minStepsTest1 = minStepsToPrevious(inputTest1)
    assertEquals(1940, energyTest1)
    assertEquals(4686774924L, minStepsTest1)

    val input = File("src/advent/year2019/day12/input.txt").readLines()
    val energy = totalEnergy(input, 1000)
    val minSteps = minStepsToPrevious(input)
    println("$energy\n$minSteps\n")
}
