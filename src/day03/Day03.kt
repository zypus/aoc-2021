package day03

import AoCTask

// https://adventofcode.com/2021/day/3

private fun countOnes(input: List<String>) = input.map {
    it.map { c -> if (c == '1') 1 else 0 }
}.reduce { acc, ints ->
    acc.zip(ints).map { (a, b) -> a + b }
}

enum class BitCriteria {
    MOST_COMMON, LEAST_COMMON
}

fun filterByBitCriteria(input: List<String>, index: Int, criteria: BitCriteria): String {
    val halfSize = input.size.toDouble() / 2
    val counts = countOnes(input)
    val countAtIndex = counts[index]
    val targetBit = when {
        countAtIndex > halfSize && criteria == BitCriteria.MOST_COMMON -> '1'
        countAtIndex < halfSize && criteria == BitCriteria.MOST_COMMON -> '0'
        countAtIndex.toDouble() == halfSize && criteria == BitCriteria.MOST_COMMON -> '1'
        countAtIndex > halfSize && criteria == BitCriteria.LEAST_COMMON -> '0'
        countAtIndex < halfSize && criteria == BitCriteria.LEAST_COMMON -> '1'
        countAtIndex.toDouble() == halfSize && criteria == BitCriteria.LEAST_COMMON -> '0'
        else -> throw Error("Shouldn't happen")
    }

    val filtered = input.filter {
        it[index] == targetBit
    }

    return if (filtered.size == 1) {
        filtered.first()
    } else if (filtered.isNotEmpty() && index < counts.size - 1) {
        filterByBitCriteria(filtered, index + 1, criteria)
    } else {
        throw Error("Couldn't find number by bit criteria")
    }
}

fun part1(input: List<String>): Int {
    val halfSize = input.size.toDouble() / 2
    val counts = countOnes(input)
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
    val oxygenGeneratorRating = filterByBitCriteria(input, 0, BitCriteria.MOST_COMMON)
    val co2ScrubberRating = filterByBitCriteria(input, 0, BitCriteria.LEAST_COMMON)
    return oxygenGeneratorRating.toInt(2) * co2ScrubberRating.toInt(2)
}

fun main() = AoCTask("day03").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    println(part1(input))
    println(part2(input))
}
