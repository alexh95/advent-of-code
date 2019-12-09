package advent.year2019.day9

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun getProgramCode(code: String): LongArray = code.split(',').map { it.toLong() }.toLongArray()

fun getProgramCodeFromFile(path: String): LongArray = getProgramCode(File(path).readText().trim())

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

    operator fun invoke(input: Long = 0L): LongArray {
        val result: MutableList<Long> = mutableListOf()

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
                    setParam1(input)
                    instructionPointer += 2L
                }
                4L -> {
                    result += param1()
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

        return result.toLongArray()
    }
}

fun main() {
    val programCodeTest00 = getProgramCode("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99")
    val outputTest00 = Program(programCodeTest00)()
    assertTrue(programCodeTest00 contentEquals outputTest00)

    val programCodeTest01 = getProgramCode("1102,34915192,34915192,7,4,7,99,0")
    val outputTest01 = Program(programCodeTest01)()
    println(outputTest01.contentToString())

    val programCodeTest02 = getProgramCode("104,1125899906842624,99")
    val outputTest02 = Program(programCodeTest02)()
    assertEquals(1125899906842624L, outputTest02[0])

    val programCodeMain = getProgramCodeFromFile("src/advent/year2019/day9/input.txt")
    val outputMain0 = Program(programCodeMain)(1)
    println(outputMain0.contentToString())
    val outputMain1 = Program(programCodeMain)(2)
    println(outputMain1.contentToString())
}