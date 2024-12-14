fun solveDay11(input: List<String>): Solution {
    val stones = input[0].split(" ").mapToLong()
    val stoneOccurrence = stones.associateBy( { it }, { stones.count { stone -> stone == it }.toLong()}).toMutableMap()
    val stonesAfterBlinking25 = countStonesAfterBlinking(stoneOccurrence, 25)
    val stonesAfterBlinking75 = countStonesAfterBlinking(stoneOccurrence, 50)
    return Solution(stonesAfterBlinking25, stonesAfterBlinking75)
}

private fun countStonesAfterBlinking(stones: MutableMap<Long, Long>, times: Int): Long {
    repeat(times) {
        stones.toMap().forEach { (stone, occurrence) ->
            if (stone == 0L) {
                stones[0L] = stones.getOrDefault(0L, 0L) - occurrence
                stones[1L] = stones.getOrDefault(1L, 0L) + occurrence
            } else {
                val digitCount = countDigits(stone)
                if (digitCount and 1 == 0) {
                    val divisor = pow10[digitCount shr 1]
                    val firstHalf = stone / divisor
                    val secondHalf = stone % divisor
                    stones[stone] = stones.getOrDefault(stone, 0L) - occurrence
                    stones[firstHalf] = stones.getOrDefault(firstHalf, 0L) + occurrence
                    stones[secondHalf] = stones.getOrDefault(secondHalf, 0L) + occurrence
                } else {
                    stones[stone] = stones.getOrDefault(stone, 0L) - occurrence
                    stones[2024 * stone] = stones.getOrDefault(2024 * stone, 0L) + occurrence
                }
            }
        }
    }
    return stones.values.sum()
}

private val pow10 = arrayOf(
    1L,
    10L,
    100L,
    1000L,
    10000L,
    100000L,
    1000000L,
    10000000L,
    100000000L,
    1000000000L,
    10000000000L,
    100000000000L,
    1000000000000L,
    10000000000000L,
    100000000000000L,
    1000000000000000L,
    10000000000000000L,
    100000000000000000L,
    1000000000000000000L,
)

private fun countDigits(value: Long): Int {
    return when {
        value <= 9L -> 1
        value <= 99L -> 2
        value <= 999L -> 3
        value <= 9999L -> 4
        value <= 99999L -> 5
        value <= 999999L -> 6
        value <= 9999999L -> 7
        value <= 99999999L -> 8
        value <= 999999999L -> 9
        value <= 9999999999L -> 10
        value <= 99999999999L -> 11
        value <= 999999999999L -> 12
        value <= 9999999999999L -> 13
        value <= 99999999999999L -> 14
        value <= 999999999999999L -> 15
        value <= 9999999999999999L -> 16
        value <= 99999999999999999L -> 17
        value <= 999999999999999999L -> 18
        else -> 19
    }
}
