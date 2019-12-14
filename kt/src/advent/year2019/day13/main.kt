package advent.year2019.day13

import java.io.File

fun getProgramCode(code: String): LongArray = code.split(',').map { it.toLong() }.toLongArray()

fun getProgramCodeFromFile(path: String): LongArray = getProgramCode(File(path).readText().trim())

fun sign(n: Int): Long = if (n > 0) 1L else if (n == 0) 0L else -1L

class Instruction(instruction: Long) {
    val opcode = instruction % 100
    val mode1 = (instruction / 100) % 10
    val mode2 = (instruction / 1000) % 10
    val mode3 = (instruction / 10000) % 10
}

class Program(programCode: LongArray) {
    private val program = LongArray(4096) { if (it < programCode.size) programCode[it] else 0 }
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

    operator fun invoke(play: Boolean): Long {
        var outputCount = 0
        var blockTileCount = 0L
        var lastX = 0L
        var lastY = 0L
        var lastScore = 0L

        var paddleX = -1
        var ballX = -1

        if (play) {
            program[0] = 2L
        }
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
                    val input = sign(ballX - paddleX)
                    setParam1(input)
                    instructionPointer += 2L
                }
                4L -> {
                    val output = param1()
                    when (outputCount) {
                        0 -> lastX = output
                        1 -> lastY = output
                        2 -> {
                            if (lastX == -1L && lastY == 0L) {
                                lastScore = output
                            } else {
                                val x = lastX.toInt()
                                when (output.toInt()) {
                                    2 -> if (!play) ++blockTileCount
                                    3 -> paddleX = x
                                    4 -> ballX = x
                                }
                            }
                        }
                    }
                    if (++outputCount >= 3) {
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

        return if (play) lastScore else blockTileCount
    }
}

fun main() {
    val programCodeMain = getProgramCodeFromFile("src/advent/year2019/day13/input.txt")
    val blockTileCount = Program(programCodeMain)(false)
    val score = Program(programCodeMain)(true)
    println("$blockTileCount\n$score\n")
}
