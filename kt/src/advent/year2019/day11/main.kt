package advent.year2019.day11

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun getProgramCode(code: String): LongArray = code.split(',').map { it.toLong() }.toLongArray()

fun getProgramCodeFromFile(path: String): LongArray = getProgramCode(File(path).readText().trim())

class V2i(val x: Int, val y: Int) {
    operator fun plus(a: V2i): V2i = V2i(x + a.x, y + a.y)
    operator fun minus(a: V2i): V2i = V2i(x - a.x, y - a.y)
    operator fun div(n: Int): V2i = V2i(x / n, y / n)
    fun turnLeft90(): V2i = V2i(y, -x)
    fun turnRight90(): V2i = V2i(-y, x)
    override fun equals(other: Any?): Boolean = x == (other as V2i).x && y == other.y
    override fun hashCode(): Int = x * 31 + y * 7
}

class Instruction(instruction: Long) {
    val opcode = instruction % 100
    val mode1 = (instruction / 100) % 10
    val mode2 = (instruction / 1000) % 10
    val mode3 = (instruction / 10000) % 10
}

class Program(programCode: LongArray) {
    private val program = LongArray(2048) { if (it < programCode.size) programCode[it] else 0 }
    private var instructionPointer = 0L
    private var relativeBase = 0L

    private fun getParameter(instructionPointer: Long, instructionMode: Long): Long {
        return when (instructionMode) {
            0L -> program[program[instructionPointer.toInt()].toInt()]
            1L -> program[instructionPointer.toInt()]
            2L -> program[(program[instructionPointer.toInt()] + relativeBase).toInt()]
            else -> throw RuntimeException("invalid instructionMode")
        }
    }

    private fun setParameter(instructionPointer: Long, instructionMode: Long, value: Long) {
        when (instructionMode) {
            0L -> program[program[instructionPointer.toInt()].toInt()] = value
            1L -> throw RuntimeException("invalid instructionMode")
            2L -> program[(program[instructionPointer.toInt()] + relativeBase).toInt()] = value
        }
    }

    operator fun invoke(): Pair<Int, List<String>> {
        var position = V2i(0, 0)
        var direction = V2i(0, -1)
        val paintedPanels: MutableMap<V2i, Long> = mutableMapOf(Pair(position, 1L))
        var outputCount = 0

        var notFinished = true
        while (notFinished) {
            val instruction = Instruction(program[instructionPointer.toInt()])
            val param1: () -> Long = { getParameter(instructionPointer + 1L, instruction.mode1) }
            val param2: () -> Long = { getParameter(instructionPointer + 2L, instruction.mode2) }
            val setParam1: (Long) -> Unit = { setParameter(instructionPointer + 1L, instruction.mode1, it) }
            val setParam3: (Long) -> Unit = { setParameter(instructionPointer + 3L, instruction.mode3, it) }
            when (instruction.opcode) {
                1L -> {
                    val value: Long = param1() + param2()
                    setParam3(value)
                    instructionPointer += 4L
                }
                2L -> {
                    val value: Long = param1() * param2()
                    setParam3(value)
                    instructionPointer += 4L
                }
                3L -> {
                    val input = paintedPanels[position] ?: 0L
                    setParam1(input)
                    instructionPointer += 2L
                }
                4L -> {
                    val output = param1()
                    if (outputCount == 0) {
                        paintedPanels[position] = output
                    } else {
                        direction = if (output == 0L) {
                            direction.turnLeft90()
                        } else {
                            direction.turnRight90()
                        }
                        position += direction
                    }
                    if (++outputCount >= 2) {
                        outputCount = 0
                    }
                    instructionPointer += 2L
                }
                5L -> {
                    if (param1() != 0L) {
                        instructionPointer = param2()
                    } else {
                        instructionPointer += 3L
                    }
                }
                6L -> {
                    if (param1() == 0L) {
                        instructionPointer = param2()
                    } else {
                        instructionPointer += 3L
                    }
                }
                7L -> {
                    val value: Long = if (param1() < param2()) 1L else 0L
                    setParam3(value)
                    instructionPointer += 4L
                }
                8L -> {
                    val value: Long = if (param1() == param2()) 1L else 0L
                    setParam3(value)
                    instructionPointer += 4L
                }
                9L -> {
                    relativeBase += param1()
                    instructionPointer += 2L
                }
                99L -> {
                    notFinished = false
                }
            }
        }

        val paintedPanelCount = paintedPanels.size
        val minPosition = paintedPanels.keys.reduce { acc, p -> V2i(min(acc.x, p.x), min(acc.y, p.y)) }
        val maxPosition = paintedPanels.keys.reduce { acc, p -> V2i(max(acc.x, p.x), max(acc.y, p.y)) }
        val hull = (minPosition.y..maxPosition.y).map { y -> (minPosition.x..maxPosition.x).map { x -> paintedPanels[V2i(x, y)] ?: 0L }.map { if (it == 0L) '.' else '#' }.joinToString("") { it.toString() } }
        return Pair(paintedPanelCount, hull)
    }
}

fun main() {
    val programCodeMain = getProgramCodeFromFile("src/advent/year2019/day11/input.txt")
    val outputMain = Program(programCodeMain)()
    println("${outputMain.first}\n${outputMain.second.joinToString("\n")}\n")
}
