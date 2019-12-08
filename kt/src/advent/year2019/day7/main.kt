package advent.year2019.day7

import java.io.File
import kotlin.test.assertEquals

fun getProgramCode(code: String): IntArray = code.split(',').map { it.toInt() }.toIntArray()

fun getProgramCodeFromFile(path: String): IntArray = getProgramCode(File(path).readText().trim())

class Instruction(val opcode: Int, val mode0: Int, val mode1: Int, val mode2: Int) {
    companion object {
        operator fun invoke(instruction: Int): Instruction {
            val opcode = instruction % 100
            val mode0 = (instruction / 100) % 10
            val mode1 = (instruction / 1000) % 10
            val mode2 = (instruction / 10000) % 10
            return Instruction(opcode, mode0, mode1, mode2)
        }
    }
}

fun IntArray.swap(index0: Int, index1: Int) {
    val temp: Int = this[index0]
    this[index0] = this[index1]
    this[index1] = temp
}

class PhaseSequence(val sequence: IntArray) {
    companion object {
        private fun permute(result: MutableList<IntArray>, sequence: IntArray, leftIndex: Int, rightIndex: Int) {
            if (leftIndex == rightIndex) {
                result += sequence.clone()
            } else {
                (leftIndex..rightIndex).forEach {
                    sequence.swap(leftIndex, it)
                    permute(result, sequence, leftIndex + 1, rightIndex)
                    sequence.swap(leftIndex, it)
                }
            }
        }
        fun allPermutations(): List<PhaseSequence> {
            val result: MutableList<IntArray> = mutableListOf()
            permute(result, arrayOf(0, 1, 2, 3, 4).toIntArray(), 0 ,4)
            return result.map { PhaseSequence(it) }
        }
    }

    fun runProgram(programCode: IntArray): PhaseSequenceResult {
        var value: Int = 0
        repeat(5) {
            value = runProgram(programCode.clone(), sequence[it], value)
        }
        return PhaseSequenceResult(this, value)
    }

    override fun toString(): String {
        return "PhaseSequence(sequence=${sequence.contentToString()})"
    }
}

typealias PhaseSequenceResult = Pair<PhaseSequence, Int>

val PhaseSequenceResult.phaseSequence
    get() = first

val PhaseSequenceResult.value
    get() = second

fun List<PhaseSequenceResult>.max() = reduceRight { pair, acc -> if (pair.value < acc.value) acc else pair }

fun getParamValue(program: IntArray, parameterValue: Int, parameterMode: Int): Int {
    return if (parameterMode == 0) program[parameterValue] else parameterValue
}

fun runProgram(program: IntArray, phase: Int, input: Int): Int {
    var result: Int = -1
    var firstInput: Boolean = true

    var instructionPointer: Int = 0
    var notFinished: Boolean = true
    while (notFinished) {
        val instruction = Instruction(program[instructionPointer])
        when (instruction.opcode) {
            1 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                val param1: Int = getParamValue(program, program[instructionPointer + 2], instruction.mode1)
                val param2: Int = program[instructionPointer + 3]
                program[param2] = param0 + param1
                instructionPointer += 4
            }
            2 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                val param1: Int = getParamValue(program, program[instructionPointer + 2], instruction.mode1)
                val param2: Int = program[instructionPointer + 3]
                program[param2] = param0 * param1
                instructionPointer += 4
            }
            3 -> {
                val param0: Int = program[instructionPointer + 1]
                if (firstInput) {
                    program[param0] = phase
                    firstInput = false
                } else {
                    program[param0] = input
                }
                instructionPointer += 2
            }
            4 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                result = param0
                instructionPointer += 2
            }
            5 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                if (param0 != 0) {
                    val param1: Int = getParamValue(program, program[instructionPointer + 2], instruction.mode1)
                    instructionPointer = param1
                } else {
                    instructionPointer += 3
                }
            }
            6 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                if (param0 == 0) {
                    val param1: Int = getParamValue(program, program[instructionPointer + 2], instruction.mode1)
                    instructionPointer = param1
                } else {
                    instructionPointer += 3
                }
            }
            7 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                val param1: Int = getParamValue(program, program[instructionPointer + 2], instruction.mode1)
                val param2: Int = program[instructionPointer + 3]
                program[param2] = if (param0 < param1) 1 else 0
                instructionPointer += 4
            }
            8 -> {
                val param0: Int = getParamValue(program, program[instructionPointer + 1], instruction.mode0)
                val param1: Int = getParamValue(program, program[instructionPointer + 2], instruction.mode1)
                val param2: Int = program[instructionPointer + 3]
                program[param2] = if (param0 == param1) 1 else 0
                instructionPointer += 4
            }
            99 -> {
                notFinished = false
            }
        }
    }

    return result
}

fun main() {
    val programCode0 = getProgramCode("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")
    val max0: PhaseSequenceResult = PhaseSequence.allPermutations().map { it.runProgram(programCode0) }.max()
    assert(max0.phaseSequence.sequence contentEquals intArrayOf(4, 3, 2, 1, 0))
    assertEquals(max0.value, 43210)

    val programCode1 = getProgramCode("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0")
    val max1: PhaseSequenceResult = PhaseSequence.allPermutations().map { it.runProgram(programCode1) }.max()
    assert(max1.phaseSequence.sequence contentEquals intArrayOf(0, 1, 2, 3, 4))
    assertEquals(max1.value, 54321)

    val programCode2 = getProgramCode("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0")
    val max2: PhaseSequenceResult = PhaseSequence.allPermutations().map { it.runProgram(programCode2) }.max()
    assert(max2.phaseSequence.sequence contentEquals intArrayOf(0, 1, 2, 3, 4))
    assertEquals(max2.value, 65210)

    val programCode = getProgramCodeFromFile("src/advent/year2019/day7/input.txt")
    val max: PhaseSequenceResult = PhaseSequence.allPermutations().map { it.runProgram(programCode) }.max()
    println(max)
}
