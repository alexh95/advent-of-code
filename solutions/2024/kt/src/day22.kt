fun solveDay22(input: List<String>): Solution {
    val secretNumbers = input.mapToLong()
    val secrets = secretNumbers.map { pseudoRandomSequence(it, 2000) }
    val secretSum = secrets.sumOf { it.last() }
    val bestDeal = findBestDeal(secrets)
    return Solution(secretSum, bestDeal)
}

private fun findBestDeal(secrets: List<List<Long>>): Long {
    val bananasDeals = secrets.map { sequence -> sequence.map { it % 10 } }
    val dealSequences = bananasDeals.map { embedSequences(it) }
    val sequenceToDeals = dealSequences.map { deal -> deal.associateBy({ it.second }, { it.first }) }
    val allSequences = sequenceToDeals.flatMap { it.keys }.distinct()
    val sequenceToValue = allSequences.map { findFirstDealForSequence(sequenceToDeals, it) }
    return sequenceToValue.max()
}

private fun findFirstDealForSequence(sequenceToDeals: List<Map<List<Long>, Long>>, sequence: List<Long>): Long {
    return sequenceToDeals.sumOf { if (it.contains(sequence)) it[sequence]!! else 0 }
}

private fun embedSequences(deal: List<Long>): List<Pair<Long, List<Long>>> {
    return deal.mapIndexed { index, s -> Pair(s, if (index >= 4) listOf(s - deal[index - 1], deal[index - 1] - deal[index - 2], deal[index - 2] - deal[index - 3], deal[index - 3] - deal[index - 4]) else null) }
        .drop(4)
        .map { (bananas, sequence) -> Pair(bananas, sequence!!) }
        .distinctBy { it.second }
}

private fun pseudoRandomSequence(value: Long, times: Int): List<Long> {
    val result = mutableListOf(value)
    repeat(times) {
        result.add(nextPseudoRandom(result.last()))
    }
    return result
}

private const val PRUNE_MODULO = 16777216

private fun nextPseudoRandom(value: Long): Long {
    var result = value
    result = ((result shl 6) xor result) % PRUNE_MODULO
    result = ((result shr 5) xor result) % PRUNE_MODULO
    result = ((result shl 11) xor result) % PRUNE_MODULO
    return result
}
