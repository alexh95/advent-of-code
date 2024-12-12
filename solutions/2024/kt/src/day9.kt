fun solveDay9(input: List<String>): Solution {
//    val input = listOf("2333133121414131402")
    val compactDiskMap = input[0]
    val sectors = calculateSectorData(compactDiskMap)
    val checksum = compressDiskMap(sectors)
    val defragChecksum = defragDiskMap(sectors)
    return Solution(checksum, defragChecksum)
}

private fun calculateSectorData(compactDiskMap: String): List<Pair<Int, Int>> {
    var idCount = 0
    return compactDiskMap.mapIndexed { index, char ->
        val sectorSize = char - '0'
        val sectorId = if (index and 1 == 0) idCount++ else -1
        Pair(sectorId, sectorSize)
    }
}

private fun expandMap(sectors: List<Pair<Int, Int>>): IntArray {
    return sectors.flatMapIndexed { index, (id, size) ->
        val sectorId = if (index and 1 == 0) id else -1
        (0 until size).map { sectorId }
    }.toIntArray()
}

private fun compressDiskMap(sectors: List<Pair<Int, Int>>): Long {
    val diskMap = expandMap(sectors)
    var leftIndex = 0
    var rightIndex = diskMap.lastIndex
    while (leftIndex < rightIndex) {
        if (diskMap[leftIndex] > -1) {
            ++leftIndex
        } else if (diskMap[rightIndex] == -1) {
            --rightIndex
        } else {
            diskMap[leftIndex++] = diskMap[rightIndex]
            diskMap[rightIndex--] = -1
        }
    }
    return diskMapChecksum(diskMap)
}

private fun defragDiskMap(sectors: List<Pair<Int,Int>>): Long {
    val diskMap = expandMap(sectors)
    val fileIdToDiskMap = mutableListOf<Pair<Int, Int>>()
    val freeSectors = mutableListOf<IntArray>()
    val lastId = sectors.last().first

    var currentIndex = 0
    sectors.forEach { (id, size) ->
        if (id > -1) {
            fileIdToDiskMap += Pair(currentIndex, size)
        } else if (size > 0) {
            freeSectors += listOf(currentIndex, size).toIntArray()
        }
        currentIndex += size
    }

    for (fileId in lastId downTo 0) {
        val file = fileIdToDiskMap[fileId]
        val firstFreeSectorIndex = freeSectors.indexOfFirst { it[1] >= file.second }
        if (firstFreeSectorIndex > -1) {
            val firstFreeSector = freeSectors[firstFreeSectorIndex]
            if (firstFreeSector[0] < file.first) {
                for (index in 0 until file.second) {
                    diskMap[index + firstFreeSector[0]] = fileId
                    diskMap[index + file.first] = -1
                }
                freeSectors[firstFreeSectorIndex][0] += file.second
                freeSectors[firstFreeSectorIndex][1] -= file.second
            }
        }
    }

    return diskMapChecksum(diskMap)
}

private fun diskMapChecksum(diskMap: IntArray): Long {
    return diskMap
        .map { it.toLong() }
        .reduceIndexed { index, acc, id -> acc + if (id > -1) index * id else 0 }
}
