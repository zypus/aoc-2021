package day14

import AoCTask
import java.util.*

// https://adventofcode.com/2021/day/14

fun part1(input: List<String>): Int {
    val (polymer, insertions) = parseIntoPolymerAndInsertions(input)
    val sequence = createPolymerSequence(polymer, insertions)
    val result = sequence.take(1 + 10).last()
    val occurrences = result.toCharArray().groupBy { it }.mapValues { it.value.size }
    val mostCommon = occurrences.maxOf { it.value }
    val leastCommon = occurrences.minOf { it.value }
    return mostCommon - leastCommon
}

private fun createPolymerSequence(
    polymer: String,
    insertions: Map<String, String>
) = generateSequence(polymer) { poly ->
    poly.windowed(2, step = 1) {
        val pair = it.toString()
        val insert = insertions.getOrDefault(pair, "")
        it.first() + insert
    }.joinToString("") + poly.last()
}

private fun createPolymerSequenceEfficient(
    polymer: String,
    insertions: Map<String, String>
): Sequence<Map<String, Long>> {
    val initialPairs = polymer.windowed(2, step = 1).groupBy { it }.mapValues { it.value.size.toLong() }
    return generateSequence(initialPairs) { pairing ->
        val newPairs = mutableMapOf<String, Long>()
        pairing.forEach { (pair, count) ->
            val insert = insertions[pair]!!
            val first = pair.first() + insert
            val second = insert + pair.last()
            newPairs[first] = newPairs.getOrDefault(first, 0L) + count
            newPairs[second] = newPairs.getOrDefault(second, 0L) + count
        }
        newPairs
    }
}


fun part2(input: List<String>): Long {
    val (polymer, insertions) = parseIntoPolymerAndInsertions(input)
    val sequence = createPolymerSequenceEfficient(polymer, insertions)
    val result = sequence.take(1 + 40).last()
    val chars = result.keys.joinToString("").toCharArray().distinct()
    val occurrences = chars.associateWith { char -> result.filter { char == it.key.first() }.values.sum() }.toMutableMap()
    occurrences[polymer.last()] = occurrences.getOrDefault(polymer.last(), 0L) + 1
    val mostCommon = occurrences.maxOf { it.value }
    val leastCommon = occurrences.minOf { it.value }
    return mostCommon - leastCommon
}

fun parseIntoPolymerAndInsertions(input: List<String>): Pair<String, Map<String, String>> {
    val polymer = input.first()
    val insertions = input.drop(2).map {
        val (left, right) = it.split(" -> ")
        left to right
    }.toMap()
    return polymer to insertions
}

fun main() = AoCTask("day14").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529)

    println(part1(input))
    println(part2(input))
}
