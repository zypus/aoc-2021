package day07

import AoCTask
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

// https://adventofcode.com/2021/day/7

fun part1(input: List<String>): Int {
    val crabs = input.first().split(",").map(String::toInt)
    val min = crabs.minOrNull()!!
    val max = crabs.maxOrNull()!!
    val costFunction = {it: Int, target: Int -> abs(it - target)}
    val bestTarget = (min..max).minByOrNull { target ->
        computeFuelConsumption(crabs, target, costFunction)
    }!!
    return computeFuelConsumption(crabs, bestTarget, costFunction)
}

private fun computeFuelConsumption(crabs: List<Int>, target: Int, costFunction: (position: Int, target:Int) -> Int): Int {
    return crabs.sumOf { costFunction(it, target) }
}

fun part2(input: List<String>): Int {
    val crabs = input.first().split(",").map(String::toInt)
    val min = crabs.minOrNull()!!
    val max = crabs.maxOrNull()!!
    val costFunction = {it: Int, target: Int -> factorial( abs(it - target))}
    val bestTarget = (min..max).minByOrNull { target ->
        computeFuelConsumption(crabs, target, costFunction)
    }!!
    return computeFuelConsumption(crabs, bestTarget, costFunction)
}

private fun factorial(x: Int): Int {
    return (1..x).sum()
}

fun main() = AoCTask("day07").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    println(part1(input))
    println(part2(input))
}
