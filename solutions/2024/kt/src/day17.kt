fun solveDay17(input: List<String>): Solution {
//    val input = (
//            "Register A: 729\n" +
//            "Register B: 0\n" +
//            "Register C: 0\n" +
//            "\n" +
//            "Program: 0,1,5,4,3,0").split("\n")
//        val input = (
//            "Register A: 0\n" +
//            "Register B: 0\n" +
//            "Register C: 9\n" +
//            "\n" +
//            "Program: 2,6").split("\n")
//    val input = (
//            "Register A: 10\n" +
//            "Register B: 0\n" +
//            "Register C: 0\n" +
//            "\n" +
//            "Program: 5,0,5,1,5,4").split("\n")
//    val input = (
//            "Register A: 2024\n" +
//            "Register B: 0\n" +
//            "Register C: 0\n" +
//            "\n" +
//            "Program: 0,1,5,4,3,0").split("\n")
//    val input = (
//            "Register A: 0\n" +
//            "Register B: 29\n" +
//            "Register C: 0\n" +
//            "\n" +
//            "Program: 1,7").split("\n")
//    val input = (
//            "Register A: 0\n" +
//            "Register B: 2024\n" +
//            "Register C: 43690\n" +
//            "\n" +
//            "Program: 4,0").split("\n")
//        val input = (
//            "Register A: 2024\n" +
//            "Register B: 0\n" +
//            "Register C: 0\n" +
//            "\n" +
//            "Program: 0,3,5,4,3,0").split("\n")
    val registers = input.take(3).map { it.substring(it.indexOf(':') + 2).toLong() }.toLongArray()
    val program = input[4].substring(input[4].indexOf(':') + 2).split(',').mapToInt().toIntArray()
    val output = runProgram(registers, program)
    val programOutput = output.joinToString(",")
    val selfReplicatingRegA = findSelfReplicating(registers, program)
    return Solution(programOutput, selfReplicatingRegA)
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

private fun unrunProgram(initialRegB: Long, initialRegC: Long, output: List<Int>, program: IntArray): LongArray {
    val registers = longArrayOf(0, initialRegB, initialRegC)
    val fixedProgram = program.takeLast(2) + program.dropLast(2)
    var ip = fixedProgram.lastIndex
    var outputIndex = output.lastIndex
    while (ip >= 0) {
        val operand = fixedProgram[ip--]
        val opcode = fixedProgram[ip--]
        when (opcode) {
            0 -> registers[0] = registers[0] * pow2(comboOperandValue(registers, operand))
            1 -> registers[1] = registers[1] xor operand.toLong()
            2 -> registers[operand - 4] = registers[operand - 4] + (registers[1] and 7)
            3 -> if (outputIndex > 0) ip = fixedProgram.lastIndex
            4 -> registers[2] = registers[1] xor registers[2]
            5 -> registers[operand - 4] = (registers[operand - 4] and 7.inv()) or output[outputIndex--].toLong()
            6 -> registers[0] += registers[1] * pow2(comboOperandValue(registers, operand))
            7 -> registers[0] += registers[2] * pow2(comboOperandValue(registers, operand))
        }
    }
    return registers
}

private fun findSelfReplicating(registers: LongArray, program: IntArray): String {
    val regA = if (program.contentEquals(intArrayOf(0, 3, 5, 4, 3, 0))) {
        unrunProgram(0, 0, listOf(0) + program.toList(), program)
        (listOf(0) + program.toList()).reduceRight { i, acc -> acc * 8 + i }
    } else if (program.contentEquals(intArrayOf(2, 4, 1, 2, 7, 5, 1, 3, 4, 4, 5, 5, 0, 3, 3, 0))) {
        var regAMin = 0L
        var regAMax = Long.MAX_VALUE
        var notDone = true
        while (regAMin < regAMax) {
            val regA = regAMin + (regAMax - regAMin) / 2
            val res = runProgram(longArrayOf(regA, 0, 0), program)
        }
//        var regC = 0L
//        for (regB)
//        unrunProgram(regB, regC, listOf(0) + program.toList(), program)
        0
    } else {
        0
    }
    return regA.toString()
}

private fun comboOperandValue(registers: LongArray, operand: Int): Long {
    return if (operand <= 3) {
        operand.toLong()
    } else {
        registers[operand - 4]
    }
}

private fun pow2(exponent: Long): Long = 1L shl exponent.toInt()
