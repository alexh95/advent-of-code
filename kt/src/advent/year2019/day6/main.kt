package advent.year2019.day6

import java.io.File

typealias OrbitTree = Pair<Int, Int>

val OrbitTree.objects
    get() = first

val OrbitTree.orbits
    get() = second

operator fun OrbitTree.plus(orbitTree: OrbitTree) = Pair(objects + orbitTree.objects, orbits + orbitTree.orbits)

fun Iterable<OrbitTree>.sum() = reduce { acc, pair -> acc + pair }

fun countOrbitsRecursive(orbitMap: Map<String, List<String>>, currentObject: String): Pair<Int, Int> {
    return if (orbitMap.containsKey(currentObject)) {
        val orbitTreeCount: OrbitTree = orbitMap[currentObject]?.map { countOrbitsRecursive(orbitMap, it) }?.sum()!!
        orbitTreeCount + OrbitTree(1, orbitTreeCount.objects)
    } else {
        OrbitTree(1, 0)
    }
}

fun countOrbits(orbitMap: Map<String, List<String>>): Pair<Int, Int> {
    return countOrbitsRecursive(orbitMap, "COM")
}

fun countTransfers(reverseOrbitMap: Map<String, String>, srcObject: String, dstObject: String): Int {
    val srcRoots: MutableList<String> = mutableListOf()
    var srcRoot: String = srcObject
    while (reverseOrbitMap.containsKey(srcRoot)) {
        srcRoot = reverseOrbitMap[srcRoot]!!
        srcRoots.add(srcRoot)
    }
    srcRoots.reverse()
    val dstRoots: MutableList<String> = mutableListOf()
    var dstRoot: String = dstObject
    while (reverseOrbitMap.containsKey(dstRoot)) {
        dstRoot = reverseOrbitMap[dstRoot]!!
        dstRoots.add(dstRoot)
    }
    dstRoots.reverse()
    val commonRootIndex: Int = srcRoots.zip(dstRoots).indexOfLast { it.first == it.second }
    return srcRoots.size + dstRoots.size - 2 * (commonRootIndex + 1)
}

fun main() {
    val orbitMap: MutableMap<String, MutableList<String>> = mutableMapOf()
    val reverseOrbitMap: MutableMap<String, String> = mutableMapOf()
    File("src/advent/year2019/day6/input.txt").readLines().map {
//    File("src/advent/year2019/day6/test.txt").readLines().map {
//    File("src/advent/year2019/day6/test2.txt").readLines().map {
        val objects = it.split(')')
        if (!orbitMap.containsKey(objects[0])) {
            orbitMap[objects[0]] = mutableListOf()
        }
        orbitMap[objects[0]]!!.add(objects[1])
        reverseOrbitMap[objects[1]] = objects[0]
    }
    val orbits = countOrbits(orbitMap)
    val minTransfers = countTransfers(reverseOrbitMap, "YOU", "SAN")
    println("$orbits\n$minTransfers\n")
}
