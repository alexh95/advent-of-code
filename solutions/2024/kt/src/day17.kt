fun solveDay17(input: List<String>): Solution {
    val registers = input.take(3).map { it.substring(it.indexOf(':') + 2).toLong() }.toLongArray()
    val program = input[4].substring(input[4].indexOf(':') + 2).split(',').mapToInt().toIntArray()
    val output = runProgram(registers, program)
    val programOutput = output.joinToString(",")
    val selfReplicatingRegA = findSmallestSelfReplicatingRegA(program)
    return Solution(programOutput, selfReplicatingRegA.toString())
}

private fun runProgram(registers: LongArray, program: IntArray): List<Int> {
    val output = mutableListOf<Int>()
    var ip = 0
    while (ip < program.size) {
        val opcode = program[ip++]
        val operand = program[ip++]
        when (opcode) {
            0 -> registers[0] = registers[0] / pow2(comboOperandValue(registers, operand))
            1 -> registers[1] = registers[1] xor operand.toLong()
            2 -> registers[1] = comboOperandValue(registers, operand) and 7
            3 -> if (registers[0] != 0L) ip = operand
            4 -> registers[1] = registers[1] xor registers[2]
            5 -> output += comboOperandValue(registers, operand).toInt() and 7
            6 -> registers[1] = registers[0] / pow2(comboOperandValue(registers, operand))
            7 -> registers[2] = registers[0] / pow2(comboOperandValue(registers, operand))
        }
    }
    return output
}

private fun findSmallestSelfReplicatingRegA(program: IntArray): Long {
    val partialPrograms = program.indices.map { program.takeLast(it + 1).toIntArray() }
    val smallestRegAs = LongArray(partialPrograms.size)
    for ((index, partialProgram) in partialPrograms.withIndex()) {
        val initialRegA = if (index > 0) 8 * smallestRegAs[index - 1] else 0L
        smallestRegAs[index] = findSmallestSelfReplicatingForOutput(initialRegA, program, partialProgram)
    }
    return smallestRegAs.last()
}

private fun findSmallestSelfReplicatingForOutput(initialRegA: Long, program: IntArray, expectedOutput: IntArray): Long {
    var regA = initialRegA - 1
    while (regA < Long.MAX_VALUE - 1) {
        val output = runProgram(longArrayOf(++regA, 0, 0), program).toIntArray()
        if (expectedOutput.contentEquals(output)) {
            return regA
        }
    }
    return -1L
}

private fun comboOperandValue(registers: LongArray, operand: Int): Long {
    return if (operand <= 3) {
        operand.toLong()
    } else {
        registers[operand - 4]
    }
}

private fun pow2(exponent: Long): Long = 1L shl exponent.toInt()
