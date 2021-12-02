package day02

import AoCTask
import Vector2
import plus

// https://adventofcode.com/2021/day/2

enum class SubmarineDirection {
    FORWARD, DOWN, UP
}

data class SubmarineState(val pos: Vector2 = Vector2(0, 0), val aim: Int = 0) {

    fun execute(command: SubmarineCommand): SubmarineState = when (command.dir) {
        SubmarineDirection.FORWARD -> copy(pos = pos + Vector2(command.value, command.value * aim))
        SubmarineDirection.DOWN -> copy(aim = aim + command.value)
        SubmarineDirection.UP -> copy(aim = aim - command.value)
    }

}

data class SubmarineCommand(val dir: SubmarineDirection, val value: Int) {
    companion object SubmarineCommand {
        fun parse(input: String): day02.SubmarineCommand {
            val (dir, distance) = input.split(" ")
            return SubmarineCommand(
                dir = SubmarineDirection.valueOf(dir.uppercase()),
                value = distance.toInt()
            )
        }
    }

    fun toVector2() = when(dir) {
        SubmarineDirection.FORWARD -> Vector2(value, 0)
        SubmarineDirection.DOWN -> Vector2(0, value)
        SubmarineDirection.UP -> Vector2(0, -value)
    }
}

fun part1(input: List<String>): Int {
    val pos = input.map(SubmarineCommand::parse)
        .map(SubmarineCommand::toVector2)
        .reduce(Vector2::plus)
    return pos.x * pos.y
}

fun part2(input: List<String>): Int {
    val finalState = input.map(SubmarineCommand::parse)
        .fold(SubmarineState(), SubmarineState::execute)
    return finalState.pos.x * finalState.pos.y
}

fun main() = AoCTask("day02").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    println(part1(input))
    println(part2(input))
}
