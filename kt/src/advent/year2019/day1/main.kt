package advent.year2019.day1

import java.io.File

fun main() {
    var modulesFuelSum = 0
    var totalFuelSum = 0
    File("src/advent/year2019/day1/input.txt").forEachLine {
        var fuel = it.toInt() / 3 - 2
        modulesFuelSum += fuel
        totalFuelSum += fuel
        while (fuel > 0) {
            fuel = fuel / 3 - 2
            if (fuel > 0) {
                totalFuelSum += fuel;
            }
        }
    }
    File("src/advent/year2019/day1/output.txt").writeText("$modulesFuelSum\n$totalFuelSum")
}
