package day08

import AoCTask

// https://adventofcode.com/2021/day/8

fun part1(input: List<String>): Int {
    val numbers = input.map {
        val (_, number) = it.split(" | ")
        number
    }
    val count = numbers.flatMap {
        it.split(" ")
    }.count {
        val segments = it.length
        segments in listOf(2,3,4,7)
    }
    return count
}

fun part2(input: List<String>): Int {
    val resolvedNumbers = input.map {
        val (digits, number) = it.split(" | ")
        val display = SegmentDisplay(digits.split(" "))
        display.resolve(number.split(" "))
    }
    return resolvedNumbers.sum()
}

data class SegmentDisplay(val digits: List<String>) {

    private val mapping: Map<String, Int>

    init {
        val one = digits.first { it.length == 2 }
        val four = digits.first { it.length == 4 }
        val seven = digits.first { it.length == 3 }
        val eight = digits.first { it.length == 7 }
        val nine = digits.first { it.length == 6 && four.all { s -> s in it } }
        val zero = digits.first { it.length == 6 && it != nine && seven.all { s -> s in it } }
        val six = digits.first { it.length == 6 && it !in listOf(zero, nine) }
        val three = digits.first { it.length == 5 && seven.all { s -> s in it } }
        val five = digits.first { it.length == 5 && it != three && it.count { s -> s in nine } == 5 }
        val two = digits.first { it.length == 5 && it !in listOf(three, five) }

        mapping = mapOf(
            zero to 0,
            one to 1,
            two to 2,
            three to 3,
            four to 4,
            five to 5,
            six to 6,
            seven to 7,
            eight to 8,
            nine to 9
        ).mapKeys {
            it.key.charsSorted()
        }
    }

    private fun String.charsSorted() = toCharArray().sorted().joinToString("")

    fun resolve(number: List<String>): Int {
        return number.map { mapping[it.charsSorted()] }.joinToString("").toInt()
    }

}

fun main() = AoCTask("day08").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    println(part1(input))
    println(part2(input))
}
