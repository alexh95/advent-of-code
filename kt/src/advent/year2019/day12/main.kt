package advent.year2019.day12

import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals

class V3i(val x: Int, val y: Int, val z: Int) {
    constructor(array: IntArray) : this(array[0], array[1], array[2])
    constructor() : this(0, 0, 0)
    operator fun plus(v: V3i): V3i = V3i(x + v.x, y + v.y, z + v.z)
    fun absum(): Int = abs(x) + abs(y) + abs(z)
    override fun toString(): String = "<x=$x, y=$y, z=$z>"
}

fun List<V3i>.sum(): V3i = reduceRight { v, acc -> v + acc }

class Moon(val p: V3i, val dp: V3i) {
    constructor(p: V3i) : this(p, V3i())

    override fun toString(): String = "p=$p, dp=$dp"
}

fun sign(n: Int): Int = if (n > 0) 1 else if (n == 0) 0 else -1

fun totalEnergy(input: List<String>, steps: Int): Int {
    val moonPositions = input.map { V3i(it.substring(1, it.length - 1).split(", ").map { s -> s.substringAfter('=').toInt() }.toIntArray()) }
    var moons = moonPositions.map { Moon(it) }
    repeat(steps) {
        moons = moons.map {moon ->
            val newDP = moon.dp + moons.map { m -> V3i(sign(m.p.x - moon.p.x), sign(m.p.y - moon.p.y), sign(m.p.z - moon.p.z)) }.sum()
            val newP = moon.p + newDP
            Moon(newP, newDP)
        }
//        println("After step ${it + 1}:\n${moons.joinToString("\n")}")
    }
    return moons.map { it.p.absum() * it.dp.absum() }.sum()
}

fun main() {
    val inputTest00 = listOf("<x=-1, y=0, z=2>", "<x=2, y=-10, z=-7>", "<x=4, y=-8, z=8>", "<x=3, y=5, z=-1>")
    val energyTest00 = totalEnergy(inputTest00, 10)
    assertEquals(179, energyTest00)

    val inputTest01 = listOf("<x=-8, y=-10, z=0>", "<x=5, y=5, z=10>", "<x=2, y=-7, z=3>", "<x=9, y=-8, z=-3>")
    val energyTest01 = totalEnergy(inputTest01, 100)
    assertEquals(1940, energyTest01)

    val inputMain0 = File("src/advent/year2019/day12/input.txt").readLines()
    val energyMain0 = totalEnergy(inputMain0, 1000)
    println("$energyMain0\n")
}
