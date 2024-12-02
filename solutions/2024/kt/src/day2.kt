import kotlin.math.abs

fun solveDay2(input: List<String>): Pair<Int, Int> {
    val reports = input.map { it.split(" ").map { s -> s.toInt() } }
    val validReportCount = reports.sumOf { safeReportValue(it) }
    val validDampenedReportCount = reports.sumOf { dampenedReportValue(it) }
    return Pair(validReportCount, validDampenedReportCount)
}

fun safeReportValue(report: List<Int>): Int {
    return if (safeReport(report)) 1 else 0
}

fun dampenedReportValue(report: List<Int>): Int {
    for (index in report.indices) {
        val dampenedReport = report.slice(0..<index) + report.slice((index + 1)..report.lastIndex)
        if (safeReport(dampenedReport)) {
            return 1
        }
    }
    return 0
}

fun safeReport(report: List<Int>): Boolean {
    val deltas = report.zip(report.drop(1)).map { (l, r) -> l - r }
    val firstSign = sign(deltas[0])
    if (!deltas.all { sign(it) == firstSign }) {
        return false
    }
    if (!deltas.all { abs(it) in 1..3 }) {
        return false
    }
    return true
}

fun sign(value: Int): Int {
    return if (value < 0) -1
    else if (value > 0) 1
    else 0
}
