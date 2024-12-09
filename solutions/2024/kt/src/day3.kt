fun solveDay3(input: List<String>): Pair<Int, Int> {
    val text = input.joinToString("")
    val mulSum = detectMulAndSum(text)
    val activeMulSum = activeMulAndSum(text)
    return Pair(mulSum, activeMulSum)
}

private fun detectMulAndSum(text: String): Int {
    val mulPairs = Regex("mul\\((\\d+),(\\d+)\\)").findAll(text)
        .map { Pair(it.groups[1]!!.value.toInt(), it.groups[2]!!.value.toInt()) }
    return mulPairs.sumOf { it.first * it.second }
}

private fun activeMulAndSum(text: String): Int {
    val activeRegion = text.split("do()")
        .joinToString("") { findActiveRegion(it) }
    return detectMulAndSum(activeRegion)
}

private fun findActiveRegion(text: String): String {
    val inactiveRegionIndex = text.indexOf("don't")
    return if (inactiveRegionIndex == -1) {
        text
    } else {
        text.substring(0, inactiveRegionIndex)
    }
}
