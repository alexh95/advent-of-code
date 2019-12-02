package advent.year2019.day2

import java.io.File

fun main() {
    val program = File("src/advent/year2019/day2/input.txt").readText().trim().split(",").map{it.toInt()}.toMutableList()
    program[1] = 12;
    program[2] = 2;
    var notFinished = true
    var instructionIndex = 0
    while (notFinished) {
        val opcode: Int = program[instructionIndex]
        when (opcode) {
            1 -> program[program[instructionIndex + 3]] = program[program[instructionIndex + 1]] + program[program[instructionIndex + 2]]
            2 -> program[program[instructionIndex + 3]] = program[program[instructionIndex + 1]] * program[program[instructionIndex + 2]]
            99 -> notFinished = false
        }
        instructionIndex += 4
        if (instructionIndex >= program.size - 3) {
            notFinished = false
        }
    }
    File("src/advent/year2019/day2/output.txt").writeText(program[0].toString())
}
