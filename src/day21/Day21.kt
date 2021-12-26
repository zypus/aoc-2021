package day21

import AoCTask

// https://adventofcode.com/2021/day/21

data class Player(val pos: Int, val score: Int)

data class State(val rolls: Int, val player1: Player, val player2: Player, val player1Turn: Boolean) {

    fun winningPlayer(minScore: Int): Player? = if (player1.score >= minScore) player1 else if (player2.score >= minScore) player2 else null
    fun loosingPlayer(minScore: Int): Player? = if (player1.score >= minScore) player2 else if (player2.score >= minScore) player1 else null

}

fun nextState(state: State, diceRoll: Int): State {
    val currentPlayer = if (state.player1Turn) {
        state.player1
    } else {
        state.player2
    }
    val newPos = ((currentPlayer.pos + diceRoll) % 10).takeIf { it != 0 } ?: 10
    val newScore = currentPlayer.score + newPos
    return state.copy(
        rolls = state.rolls + 3,
        player1 = if (state.player1Turn) Player(newPos, newScore) else state.player1,
        player2 = if (state.player1Turn) state.player2 else Player(newPos, newScore),
        player1Turn = !state.player1Turn
    )
}

fun playGame(initialState: State, die: (roll: Int) -> Int): Sequence<State> {
    return generateSequence(initialState) { state ->
        val rolls = state.rolls
        val diceRoll = (0..2).map { die(rolls + it) }
        val diceRollSum = diceRoll.sum()
        nextState(state, diceRollSum)
    }
}

fun part1(input: List<String>): Int {
    val (player1Start, player2Start) = parseStartingPositions(input)
    val initialState = State(0, Player(player1Start, 0), Player(player2Start, 0), true)
    val deterministicDie = { rolls: Int -> rolls % 100 + 1 }
    return playGame(initialState, deterministicDie).first {
        it.winningPlayer(1000) != null
    }.let {
        it.loosingPlayer(1000)!!.score * it.rolls
    }
}

private fun parseStartingPositions(input: List<String>) =
    input.map { it.split(": ").last().toInt() }

data class Universe(val state: State, val count: Long)

fun part2(input: List<String>): Long {
    val (player1Start, player2Start) = parseStartingPositions(input)
    val initialState = State(0, Player(player1Start, 0), Player(player2Start, 0), true)

    val diceOutcomes = listOf(1,2,3)
    val universesPerRoll = diceOutcomes.flatMap { first ->
        diceOutcomes.flatMap { second ->
            diceOutcomes.map { third -> first + second + third }
        }
    }.groupBy { it }.mapValues { it.value.size.toLong() }

    var universes = listOf(Universe(initialState, 1))

    var wonUniversesPlayer1 = 0L
    var wonUniversesPlayer2 = 0L

    while (universes.isNotEmpty()) {
        val (completedUniverses, openUniverses) = universes.flatMap { universe ->
            universesPerRoll.map { entry ->
                Universe(nextState(universe.state, entry.key), universe.count * entry.value)
            }
        }.partition {
            it.state.winningPlayer(21) != null
        }
        completedUniverses.forEach { universe ->
            if (universe.state.winningPlayer(21) == universe.state.player1) {
                wonUniversesPlayer1 += universe.count
            } else {
                wonUniversesPlayer2 += universe.count
            }
        }
        universes = openUniverses
    }

    return maxOf(wonUniversesPlayer1, wonUniversesPlayer2)
}

fun main() = AoCTask("day21").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315)

    println(part1(input))
    println(part2(input))
}
