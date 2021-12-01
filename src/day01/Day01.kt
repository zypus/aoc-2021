package day01

import AoCTask

// https://adventofcode.com/2021/day/1

fun part1(input: List<String>): Int {
    return input.map(String::toInt).windowed(2) {
        it[0] < it[1]
    }.count { increased -> increased }
}

fun part2(input: List<String>): Int {
    return input.map(String::toInt).windowed(3, 1) {
        it.sum()
    }.windowed(2) {
        it[0] < it[1]
    }.count { increased -> increased }
}

fun main() = AoCTask("day01").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    println(part1(input))
    println(part2(input))
}
