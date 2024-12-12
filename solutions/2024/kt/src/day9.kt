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
    val moved = mutableSetOf(-1)
    var firstEmptySectorIndex = diskMap.indexOf(-1)
    var leftIndex = firstEmptySectorIndex
    var rightIndex = diskMap.lastIndex
    while (diskMap.indexOf(-1) < rightIndex) {
        if (diskMap[leftIndex] > -1) {
            ++leftIndex
        } else if (diskMap[rightIndex] == -1 || moved.contains(diskMap[rightIndex])) {
            --rightIndex
        } else if (leftIndex >= rightIndex) {
            leftIndex = diskMap.indexOf(-1)
            val currentId = diskMap[rightIndex]
            val currentFileSize = sectors[2 * currentId].second
            rightIndex -= currentFileSize
        } else {
            val emptySectorSize = diskMap.drop(leftIndex).indexOfFirst { it > -1 }
            val currentId = diskMap[rightIndex]
            val currentFileSize = sectors[2 * currentId].second

            if (emptySectorSize >= currentFileSize && !moved.contains(currentId)) {
                moved += currentId
                for (i in 0 until currentFileSize) {
                    diskMap[leftIndex++] = diskMap[rightIndex]
                    diskMap[rightIndex--] = -1
                }
                leftIndex = diskMap.indexOf(-1)
            } else {
                leftIndex += emptySectorSize
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
