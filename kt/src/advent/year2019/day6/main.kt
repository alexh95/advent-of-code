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

fun main() {
    val orbitMap: MutableMap<String, MutableList<String>> = mutableMapOf()
    File("src/advent/year2019/day6/input.txt").readLines().map {
//    File("src/advent/year2019/day6/test.txt").readLines().map {
        val objects = it.split(')')
        if (!orbitMap.containsKey(objects[0])) {
            orbitMap[objects[0]] = mutableListOf()
        }
        orbitMap[objects[0]]!!.add(objects[1])
    }
    val orbits = countOrbits(orbitMap)
    println("$orbits\n")
}
