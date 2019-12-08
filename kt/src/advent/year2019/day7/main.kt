package advent.year2019.day7

import java.io.File
import kotlin.test.assertEquals

fun getProgramCode(code: String): IntArray = code.split(',').map { it.toInt() }.toIntArray()

fun getProgramCodeFromFile(path: String): IntArray = getProgramCode(File(path).readText().trim())

class Program(val program: IntArray, val phase: Int) {
    var firstInput: Boolean = true
    var instructionPointer = 0

    operator fun invoke(input: Int): Int? {
        var result: Int? = null

        var notFinished = true
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
                    notFinished = false
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
}

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

        fun allPermutations(startIndex: Int, stopIndex: Int): List<PhaseSequence> {
            val result: MutableList<IntArray> = mutableListOf()
            permute(result, (startIndex..stopIndex).toList().toIntArray(), 0, 4)
            return result.map { PhaseSequence(it) }
        }
    }

    operator fun get(index: Int) = sequence[index]

    override fun toString(): String {
        return "PhaseSequence(sequence=${sequence.contentToString()})"
    }
}

fun runProgram(programCode: IntArray, phaseSequence: PhaseSequence): PhaseSequenceResult {
    var value = 0
    repeat(5) {
        value = Program(programCode.clone(), phaseSequence[it])(value)!!
    }
    return PhaseSequenceResult(phaseSequence, value)
}

fun runProgramLoop(programCode: IntArray, phaseSequence: PhaseSequence): PhaseSequenceResult {
    var value = 0
    var notFinished = true
    val amplifierPrograms = List(5) { Program(programCode.clone(), phaseSequence[it]) }
    var amplifierIndex = 0
    while (notFinished) {
        val programResult: Int? = amplifierPrograms[amplifierIndex++](value)
        if (amplifierIndex >= amplifierPrograms.size) {
            amplifierIndex = 0
        }
        if (programResult != null) {
            value = programResult
        } else {
            notFinished = false
        }
    }
    return PhaseSequenceResult(phaseSequence, value)
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

fun main() {
    val programCodeTest00 = getProgramCode("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")
    val maxTest00: PhaseSequenceResult = PhaseSequence.allPermutations(0, 4).map { runProgram(programCodeTest00, it) }.max()
    assert(intArrayOf(4, 3, 2, 1, 0) contentEquals maxTest00.phaseSequence.sequence)
    assertEquals(43210, maxTest00.value)

    val programCodeTest01 = getProgramCode("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0")
    val maxTest01: PhaseSequenceResult = PhaseSequence.allPermutations(0, 4).map { runProgram(programCodeTest01, it) }.max()
    assert(intArrayOf(0, 1, 2, 3, 4) contentEquals maxTest01.phaseSequence.sequence)
    assertEquals(54321, maxTest01.value)

    val programCodeTest02 = getProgramCode("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0")
    val maxTest02: PhaseSequenceResult = PhaseSequence.allPermutations(0, 4).map { runProgram(programCodeTest02, it) }.max()
    assert(intArrayOf(0, 1, 2, 3, 4) contentEquals maxTest02.phaseSequence.sequence)
    assertEquals(65210, maxTest02.value)

    val programCodeMain0 = getProgramCodeFromFile("src/advent/year2019/day7/input.txt")
    val maxMain0: PhaseSequenceResult = PhaseSequence.allPermutations(0, 4).map { runProgram(programCodeMain0, it) }.max()
    println(maxMain0)

    val programCodeTest10 =
        getProgramCode("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5")
    val maxTest10: PhaseSequenceResult =
        PhaseSequence.allPermutations(5, 9).map { runProgramLoop(programCodeTest10, it) }.max()
    assert(intArrayOf(9, 8, 7, 6, 5) contentEquals maxTest10.phaseSequence.sequence)
    assertEquals(139629729, maxTest10.value)

    val programCodeTest11 =
        getProgramCode("3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10")
    val maxTest11: PhaseSequenceResult =
        PhaseSequence.allPermutations(5, 9).map { runProgramLoop(programCodeTest11, it) }.max()
    assert(intArrayOf(9, 8, 7, 6, 5) contentEquals maxTest11.phaseSequence.sequence)
    assertEquals(18216, maxTest11.value)

    val programCodeMain1 = getProgramCodeFromFile("src/advent/year2019/day7/input.txt")
    val maxMain1: PhaseSequenceResult = PhaseSequence.allPermutations(5, 9).map { runProgramLoop(programCodeMain1, it) }.max()
    println(maxMain1)
}
