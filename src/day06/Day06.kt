package day06

import AoCTask

// https://adventofcode.com/2021/day/6

private fun simulateFishLive(initialFish: List<Int>, days: Int): List<Int> {
    val allDays = 1..days
    val fishAfter80days = allDays.fold(initialFish) { fishes, _ ->
        var spawnCounter = 0
        val updatedFishes = fishes.map {
            when (it) {
                0 -> {
                    spawnCounter++
                    6
                }
                else -> it - 1
            }
        }
        updatedFishes + List(spawnCounter) { 8 }
    }
    return fishAfter80days
}

private fun simulateFishLive2(initialFish: List<Int>, days: Int): Map<Int, Long> {
    val allDays = 1..days
    val allTimerValues = 0..8
    var fishPerSpawnTimer = initialFish.groupBy { it }.mapValues { it.value.size.toLong() }
    allDays.forEach {
        val newTimers = mutableMapOf<Int, Long>()
        allTimerValues.forEach { timer ->
            when(timer) {
                0 -> {
                    newTimers[8] = fishPerSpawnTimer.getOrDefault(0, 0L)
                    newTimers[6] = fishPerSpawnTimer.getOrDefault(0, 0L)
                }
                else -> newTimers[timer - 1] = newTimers.getOrDefault(timer - 1, 0L) + fishPerSpawnTimer.getOrDefault(timer, 0L)
            }
        }
        fishPerSpawnTimer = newTimers
    }
    return fishPerSpawnTimer
}

fun part1(input: List<String>): Int {
    val initialFish = input.first().split(",").map(String::toInt)
    val fishAfter80days = simulateFishLive(initialFish, 80)
    return fishAfter80days.size
}

fun part2(input: List<String>): Long {
    val initialFish = input.first().split(",").map(String::toInt)
    val fishAfter256days = simulateFishLive2(initialFish, 256)
    return fishAfter256days.values.sum()
}

fun main() = AoCTask("day06").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 5934)
    check(part2(testInput) == 26984457539)

    println(part1(input))
    println(part2(input))
}
