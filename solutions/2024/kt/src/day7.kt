fun solveDay7(input: List<String>): Pair<Long, Long> {
    val expressions = input.map { Expression(it) }
    val solvableExpression2Count = expressions
        .filter { expressionIsSolvable2(it) }
        .sumOf { it.result }
    val solvableExpression3Count = expressions
        .filter { expressionIsSolvable3(it) }
        .sumOf { it.result }
    return Pair(solvableExpression2Count, solvableExpression3Count)
}

private fun expressionIsSolvable2(expression: Expression): Boolean {
    val operationCount = pow2(expression.operands.size - 1)
    for (operations in 0 until operationCount) {
        val expressionResult = expression.operands.reduceIndexed { index, acc, op ->
            if (isBitSet(operations, index - 1)) acc * op else acc + op
        }
        if (expressionResult == expression.result) {
            return true
        }
    }
    return false
}

private fun expressionIsSolvable3(expression: Expression): Boolean {
    val operationCount = pow3(expression.operands.size - 1)
    for (operations in 0 until operationCount) {
        val expressionResult = expression.operands.reduceIndexed { index, acc, op ->
            when (getBase3Component(operations, index - 1)) {
                0 -> acc + op
                1 -> acc * op
                2 -> concat(acc, op)
                else -> 0
            }
        }
        if (expressionResult == expression.result) {
            return true
        }
    }
    return false
}

private fun isBitSet(value: Int, bit: Int): Boolean {
    return ((value shr bit) and 1) == 1
}

private fun getBase3Component(value: Int, index: Int): Int {
    val shifted = value / pow3(index)
    return shifted % 3
}

private fun pow2(exponent: Int): Int = 1 shl exponent

private fun pow3(exponent: Int): Int {
    var result = 1
    for (i in 0 until exponent) {
        result *= 3
    }
    return result
}

private fun concat(a: Long, b: Long): Long {
    val value = a.toString() + b.toString()
    return value.toLong()
}

private class Expression(line: String) {
    val result: Long
    val operands: List<Long>

    init {
        val parts = line.split(": ")
        result = parts[0].toLong()
        operands = parts[1].split(" ").map { it.toLong() }
    }
}
