package advent.year2019.day3

import java.io.File
import kotlin.math.abs

data class V2i(val x: Int, val y: Int) {
    constructor() : this(0, 0)

    operator fun plus(a: V2i) = V2i(x + a.x, y + a.y)

    override fun equals(other: Any?): Boolean {
        val v: V2i = other as V2i
        return x == v.x && y == v.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

object LetterToDirection {
    private val map: HashMap<Char, V2i> = HashMap()

    init {
        map['R'] = V2i(1, 0)
        map['U'] = V2i(0, -1)
        map['L'] = V2i(-1, 0)
        map['D'] = V2i(0, 1)
    }

    fun fromLetter(l: Char): V2i? = map[l]
}

fun lineToPositions(line: String): List<V2i> {
    var currentPosition: V2i = V2i()
    val positions: MutableList<V2i> = arrayListOf()
    line.split(",").forEach {

        val direction: V2i = LetterToDirection.fromLetter(it[0])!!
        val distance: Int = it.substring(1).toInt()
        (0 until distance).forEach { _ ->
            currentPosition += direction
            positions.add(currentPosition)
        }
    }
    return positions
}

fun closestIntersection(wire1: List<V2i>, wire2: List<V2i>): Int {
    return wire1.intersect(wire2).map { abs(it.x) + abs(it.y) }.min()!!
}

class V2iIndexed(val index: Int, val position: V2i) {
    override fun equals(other: Any?): Boolean {
        val v: V2iIndexed = other as V2iIndexed
        return position == v.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

fun minSteps(wire1: List<V2i>, wire2: List<V2i>): Int {
    return wire1
            .mapIndexed { index: Int, element: V2i -> V2iIndexed(index, element)}
            .intersect(wire2.mapIndexed { index: Int, element: V2i -> V2iIndexed(index, element)})
            .map { it.index + wire2.indexOf(it.position) + 2 }
            .min()!!
}

fun main() {
    val wires = File("src/advent/year2019/day3/input.txt").readLines().map { lineToPositions(it) }
    val minIntersectionDistance: Int = closestIntersection(wires[0], wires[1])
    val minIntersectionSteps: Int = minSteps(wires[0], wires[1])
    File("src/advent/year2019/day3/output.txt").writeText("$minIntersectionDistance\n$minIntersectionSteps\n")
}
