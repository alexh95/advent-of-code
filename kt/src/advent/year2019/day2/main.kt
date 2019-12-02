package advent.year2019.day2

import java.io.File

fun calculate(program: MutableList<Int>) {
    var notFinished = true
    var instructionPointer = 0
    while (notFinished) {
        val opcode: Int = program[instructionPointer]
        when (opcode) {
            1 -> program[program[instructionPointer + 3]] = program[program[instructionPointer + 1]] + program[program[instructionPointer + 2]]
            2 -> program[program[instructionPointer + 3]] = program[program[instructionPointer + 1]] * program[program[instructionPointer + 2]]
            99 -> notFinished = false
        }
        instructionPointer += 4
        if (instructionPointer >= program.size - 3) {
            notFinished = false
        }
    }
}

fun main() {
    val originalProgram = File("src/advent/year2019/day2/input.txt").readText().trim().split(",").map{it.toInt()}
    val program = originalProgram.toMutableList()
    program[1] = 12
    program[2] = 2
    calculate(program)

    var noun = 0
    var verb = 0
    var notFinished = true
    while (notFinished) {
        val newProgram = originalProgram.toMutableList()
        newProgram[1] = noun
        newProgram[2] = verb
        calculate(newProgram)
        if (newProgram[0] == 19690720) {
            notFinished = false
        } else {
            ++noun
            if (noun > 99) {
                noun = 0
                ++verb
            }
        }
    }

    val output = 100 * noun + verb
    File("src/advent/year2019/day2/output.txt").writeText(program[0].toString() + "\n" + output.toString())
}
