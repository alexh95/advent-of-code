package advent.year2019.day5

import java.io.File

class Instruction(val opcode: Int, val mode0: Int, val mode1: Int, val mode2: Int) {
    companion object {
        fun fromInt(instruction: Int): Instruction {
            val opcode = instruction % 100
            val mode0 = (instruction / 100) % 10
            val mode1 = (instruction / 1000) % 10
            val mode2 = (instruction / 10000) % 10
            return Instruction(opcode, mode0, mode1, mode2)
        }
    }
}

fun getParamValue(program: IntArray, parameterValue: Int, parameterMode: Int): Int {
    return if (parameterMode == 0) program[parameterValue] else parameterValue
}

fun runProgram(program: IntArray) {
    var instructionPointer: Int = 0
    var notFinished: Boolean = true
    while (notFinished) {
        val instruction = Instruction.fromInt(program[instructionPointer++])
        when (instruction.opcode) {
            1 -> {
                val param0: Int = getParamValue(program, program[instructionPointer], instruction.mode0)
                val param1: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode1)
                val param2: Int = program[instructionPointer + 2]
                program[param2] = param0 + param1
                instructionPointer += 3
            }
            2 -> {
                val param0: Int = getParamValue(program, program[instructionPointer], instruction.mode0)
                val param1: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode1)
                val param2: Int = program[instructionPointer + 2]
                program[param2] = param0 * param1
                instructionPointer += 3
            }
            3 -> {
                val param0: Int = program[instructionPointer]
                program[param0] = 1
                instructionPointer += 1
            }
            4 -> {
                val param0: Int = getParamValue(program, program[instructionPointer], instruction.mode0)
                println(param0)
                instructionPointer += 1
            }
            99 -> {
                notFinished = false
            }
        }
    }
}

fun main() {
    val programCode = File("src/advent/year2019/day5/input.txt").readText().trim().split(',').map { it.toInt() }.toIntArray()
    val program = programCode.clone()
    runProgram(program)
}
