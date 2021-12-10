package day10

import AoCTask
import java.util.*

// https://adventofcode.com/2021/day/10

enum class Symbol(val char: Char, val isOpen: Boolean = true) {
    OPENING_ROUND_BRACKET('('),
    CLOSING_ROUND_BRACKET(')', false),
    OPENING_SQUARE_BRACKET('['),
    CLOSING_SQUARE_BRACKET(']', false),
    OPENING_CURLY_BRACKET('{'),
    CLOSING_CURLY_BRACKET('}', false),
    OPENING_ANGLE_BRACKET('<'),
    CLOSING_ANGLE_BRACKET('>', false),
}

val bracketPairs = listOf<Pair<Symbol, Symbol>>(
    Symbol.OPENING_ROUND_BRACKET to Symbol.CLOSING_ROUND_BRACKET,
    Symbol.OPENING_SQUARE_BRACKET to Symbol.CLOSING_SQUARE_BRACKET,
    Symbol.OPENING_CURLY_BRACKET to Symbol.CLOSING_CURLY_BRACKET,
    Symbol.OPENING_ANGLE_BRACKET to Symbol.CLOSING_ANGLE_BRACKET,
)

sealed class Result {
    data class Error(val symbol: Symbol): Result()
    data class Incomplete(val completion: List<Symbol>): Result()
    object Fine: Result()
}

fun syntaxScore(symbol: Symbol) = when(symbol) {
    Symbol.CLOSING_ROUND_BRACKET -> 3
    Symbol.CLOSING_SQUARE_BRACKET -> 57
    Symbol.CLOSING_CURLY_BRACKET -> 1197
    Symbol.CLOSING_ANGLE_BRACKET -> 25137
    else -> 0
}

fun autoScore(symbol: Symbol) = when(symbol) {
    Symbol.CLOSING_ROUND_BRACKET -> 1
    Symbol.CLOSING_SQUARE_BRACKET -> 2
    Symbol.CLOSING_CURLY_BRACKET -> 3
    Symbol.CLOSING_ANGLE_BRACKET -> 4
    else -> 0
}

fun checkLine(line: List<Symbol>): Result {
    val stack: Stack<Symbol> = Stack()
    line.forEach { symbol ->
        if (symbol.isOpen) {
            stack.push(symbol)
        } else {
            if (stack.isNotEmpty()) {
                val topOfStack = stack.pop()
                val pairing = topOfStack to symbol
                if (pairing !in bracketPairs) {
                    return Result.Error(symbol)
                }
            } else {
                return Result.Error(symbol)
            }
        }
    }
    return if (stack.isNotEmpty()) {
        Result.Incomplete(stack.reversed().map { s -> bracketPairs.first { it.first == s }.second })
    } else {
        Result.Fine
    }
}

fun part1(input: List<String>): Int {
    val lines = inputToSymbols(input)
    val score = lines.map {
        checkLine(it)
    }.filterIsInstance<Result.Error>().sumOf {
        syntaxScore( it.symbol)
    }
    return score
}

fun part2(input: List<String>): Int {
    val lines = inputToSymbols(input)
    val scores = lines.map {
        checkLine(it)
    }.filterIsInstance<Result.Incomplete>().map {
        it.completion.fold(0L) { acc, symbol ->
            acc * 5 + autoScore(symbol)
        }
    }
    return scores.sorted()[scores.size / 2].toInt()
}

private fun inputToSymbols(input: List<String>) = input.map { line ->
    line.map { c -> Symbol.values().first { it.char == c } }
}

fun main() = AoCTask("day10").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957)

    println(part1(input))
    println(part2(input))
}
