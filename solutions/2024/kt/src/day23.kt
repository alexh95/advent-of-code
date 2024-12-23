fun solveDay23(input: List<String>): Solution {
    val connections = findConnections(input)
    val triplets = connections
        .flatMap { (commonComputer, connectedComputers) -> findTriplets(connections, commonComputer, connectedComputers) }
        .distinct()
    val interestingTriplets = triplets.filter { computers -> computers.any { it.startsWith('t') } }
    val networks = connections
        .map { (common, comps) -> findLargestNetworks(connections, common, comps) }
        .distinct()
    val password = networks
        .maxBy { it.size }
        .sorted()
        .joinToString(",")
    return Solution(interestingTriplets.size.toString(), password)
}

private fun findLargestNetworks(connections: Map<String, Set<String>>, commonComputer: String, connectedComputers: Set<String>): Set<String> {
    val computers = connectedComputers.sorted()
    val result = mutableSetOf(commonComputer, computers[0])
    for (i in 1 until computers.size) {
        val candidate = computers[i]
        if (connections[candidate]!!.containsAll(result)) {
            result += candidate
        }
    }
    return result
}

private fun findTriplets(connections: Map<String, Set<String>>, commonComputer: String, connectedComputers: Set<String>): List<Set<String>> {
    val result = mutableListOf<Set<String>>()
    val computers = connectedComputers.sorted()
    for (i in 0 until computers.size - 1) {
        for (j in i + 1 until computers.size) {
            if (connections[computers[i]]!!.contains(computers[j])) {
                result += setOf(commonComputer, computers[i], computers[j])
            }
        }
    }
    return result
}

private fun findConnections(input: List<String>): Map<String, Set<String>> {
    val pairs = input.map { it.split("-").toPair() }
    val reversePairs = pairs.map { Pair(it.second, it.first) }
    val allPairs = pairs + reversePairs
    return allPairs.groupBy { it.first }.mapValues { it.value.map { pair -> pair.second }.toSet() }
}
