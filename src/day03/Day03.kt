package day03

import AoCTask

// https://adventofcode.com/2021/day/3

fun part1(input: List<String>): Int {
    val halfSize = input.size.toDouble() / 2
    val counts = input.map {
        it.map { c -> if (c == '1') 1 else 0 }
    }.reduce { acc, ints ->
        acc.zip(ints).map { (a,b) -> a + b }
    }
    val gamma = counts.map {
        if (it > halfSize) {
            '1'
        } else {
            '0'
        }
    }.joinToString("")
    val epsilon = gamma.map { c -> if (c == '1') '0' else '1' }.joinToString("")
    return gamma.toInt(2) * epsilon.toInt(2)
}

fun part2(input: List<String>): Int {
    return input.size
}

fun main() = AoCTask("day03").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 198)

    println(part1(input))
    println(part2(input))
}
