fun solveDay24(input: List<String>): Solution {
    val (initialValues, gates) = processInput(input)
    val gatesResult = resolveGates(initialValues, gates)
    val gateCode = fixGates(gates)
    return Solution(gatesResult.toString(), gateCode)
}

private fun resolveGates(initialValues: Map<String, Int>, gates: List<Gate>): Long {
    val signalToValue = initialValues.toMutableMap()
    val destinationToGate = gates.associateBy { it.destination }
    val keysToSolve = destinationToGate.keys.filter { it.matches(OUT_SIGNAL_REGEX) }.sorted()
    for (outSignal in keysToSolve) {
        resolveGate(destinationToGate[outSignal]!!, signalToValue, destinationToGate)
    }
    return keysToSolve.map { signalToValue[it]!!.toLong() }
        .reduceRight { i, acc -> i + acc * 2L }
}

private fun resolveGate(gateToSolve: Gate, signalToValue: MutableMap<String, Int>, destinationToGate: Map<String, Gate>): Int {
    val operand1Value = signalToValue.getOrElse(gateToSolve.operand1) {
        resolveGate(destinationToGate[gateToSolve.operand1]!!, signalToValue, destinationToGate)
    }
    val operand2Value = signalToValue.getOrElse(gateToSolve.operand2) {
        resolveGate(destinationToGate[gateToSolve.operand2]!!, signalToValue, destinationToGate)
    }
    val result = when (gateToSolve.operation) {
        GateOperation.AND -> operand1Value and operand2Value
        GateOperation.OR -> operand1Value or operand2Value
        GateOperation.XOR -> operand1Value xor operand2Value
    }
    signalToValue[gateToSolve.destination] = result
    return result
}

private val CLEAR_SIGNAL_REGEX = Regex(".+\\d\\d")
private val OUT_SIGNAL_REGEX = Regex("z\\d\\d")

private fun fixGates(gates: List<Gate>): String {
    val orOperands = gates.filter { it.operation == GateOperation.OR }.flatMap { listOf(it.operand1, it.operand2) }.toSet()
    val lastOutGate = gates
        .map { it.destination }
        .filter { it.startsWith("z") }
        .max()
    val wrongGates = gates
        .filter { gatePatternInvalid(it, orOperands, lastOutGate) }
        .map { it.destination }
    return wrongGates.sorted().joinToString(",")
}

private fun gatePatternInvalid(gate: Gate, orOperands: Set<String>, lastOutGate: String): Boolean {
    val isXor = gate.operation == GateOperation.XOR
    if (gate.destination.startsWith("z") && !isXor && gate.destination != lastOutGate) {
        return true
    }
    if (isXor) {
        if (!gate.operand1.matches(CLEAR_SIGNAL_REGEX) && !gate.operand2.matches(CLEAR_SIGNAL_REGEX) && !gate.destination.matches(CLEAR_SIGNAL_REGEX)) {
            return true
        }
        if (orOperands.contains(gate.destination)) {
            return true
        }
    }
    if (gate.operation == GateOperation.AND && gate.operand1 != "x00" && gate.operand2 != "y00") {
        if (!orOperands.contains(gate.destination)) {
            return true
        }
    }

    return false
}

private fun processInput(input: List<String>): Pair<Map<String, Int>, List<Gate>> {
    val separatorIndex = input.indexOf("")
    val initialSignals = input.take(separatorIndex)
        .map { it.split(": ") }
        .associate { Pair(it[0], it[1].toInt()) }
    val gates = input.drop(separatorIndex + 1).map { parseGate(it) }
    return Pair(initialSignals, gates)
}

private fun parseGate(line: String): Gate {
    val parts = line.split(" -> ")
    val leftParts = parts[0].split(" ")
    val (firstOperand, secondOperand) = listOf(leftParts[0], leftParts[2]).sorted().toPair()
    val operation = when (leftParts[1]) {
        "AND" -> GateOperation.AND
        "OR" -> GateOperation.OR
        "XOR" -> GateOperation.XOR
        else -> throw Error("Invalid operand.")
    }
    return Gate(firstOperand, secondOperand, operation, parts[1])
}

private class Gate(
    val operand1: String,
    val operand2: String,
    val operation: GateOperation,
    val destination: String
) {
    override fun toString(): String {
        return "$operand1 $operation $operand2 -> $destination"
    }
}

private enum class GateOperation {
    AND,
    OR,
    XOR,
}
