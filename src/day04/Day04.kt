package day04

import AoCTask

// https://adventofcode.com/2021/day/4

data class BingoCell(val number: Int, val marked: Boolean = false)

data class BingoBoard(val rows: List<List<BingoCell>>) {

    fun mark(numberToMark: Int): BingoBoard {
        return BingoBoard(rows.map {
            row -> row.map { cell -> cell.copy(marked = cell.marked || numberToMark == cell.number) }
        })
    }

    fun hasWon(): Boolean {
        val anyRowIsFullyMarked = rows.any { row -> row.all { it.marked } }
        val anyColumnIsFullyMarked = rows.first().indices.any { index ->
            rows.all { row -> row[index].marked }
        }
        return anyRowIsFullyMarked || anyColumnIsFullyMarked
    }

    fun unmarkedNumbers(): List<Int> {
        return rows.flatMap { row -> row.filter { !it.marked } }.map { it.number }
    }

    override fun toString(): String {
        return rows.joinToString("\n") {
            row -> row.joinToString(" ") {
                if (it.marked) {
                    "\u001B[1m\u001B[32m${it.number}\u001B[0m"
                } else {
                    it.number.toString()
                }
            }
        }
    }
}

fun part1(input: List<String>): Int {
    var (numberSequence, boards) = processInput(input)

    for (number in numberSequence) {
        boards = boards.map { it.mark(number) }
        val winner = boards.firstOrNull { it.hasWon() }
        if (winner != null) {
            println("won:\n$winner\n")
            return winner.unmarkedNumbers().reduce(Int::plus) * number
        }
    }

    throw Error("No board won")
}

private fun processInput(input: List<String>): Pair<List<Int>, List<BingoBoard>> {
    val numberSequence = input.first().split(",").map { it.toInt() }
    val boards = input.drop(1)
        .filter { it.isNotBlank() }
        .windowed(5, 5) { boardLines ->
            BingoBoard(boardLines.map { line ->
                line.trim().split("\\s+".toRegex()).map { BingoCell(it.toInt()) }
            })
        }
    return numberSequence to boards
}

fun part2(input: List<String>): Int {

    var (numberSequence, boards) = processInput(input)

    var lastOpenBoard: BingoBoard? = null

    for (number in numberSequence) {
        boards = boards.map { it.mark(number) }
        val (openBoards, winners) = boards.partition { !it.hasWon() }
        if (openBoards.isEmpty()) {
            val lastWinner = winners.first()
            println("last winner:\n$lastWinner\n")
            return lastWinner.unmarkedNumbers().reduce(Int::plus) * number
        } else {
            boards = openBoards
        }
    }

    throw Error("Not all boards won")
}

fun main() = AoCTask("day04").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    println(part1(input))
    println(part2(input))
}
