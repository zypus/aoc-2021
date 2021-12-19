package day17

import AoCTask
import Vector2
import plus

// https://adventofcode.com/2021/day/17

data class Target(val xRange: IntRange, val yRange: IntRange) {

    operator fun contains(pos: Vector2): Boolean {
        return pos.x in xRange && pos.y in yRange
    }

    fun passedThrough(posFrom: Vector2, posTo: Vector2): Boolean {
        return (posFrom.x < xRange.first && posTo.x > xRange.last) || (posFrom.y < yRange.first && posTo.y > yRange.last) ||
                (posTo.x < xRange.first && posFrom.x > xRange.last) || (posTo.y < yRange.first && posFrom.y > yRange.last)
    }

}

data class State(val pos: Vector2, val vel: Vector2)

fun simulate(state: State): State {
    val newPos = state.pos + state.vel
    val newVelX = when {
        state.vel.x < 0 -> state.vel.x + 1
        state.vel.x > 0 -> state.vel.x - 1
        else -> 0
    }
    val newVel = Vector2(newVelX, state.vel.y - 1)
    return State(newPos, newVel)
}

fun part1(input: List<String>): Int {
    val (xRangeString, yRangeString) = input.first().substringAfter(':').trim().split(", ")
    val xRange = xRangeString.split("=").last().split("..").let { it[0].toInt()..it[1].toInt() }
    val yRange = yRangeString.split("=").last().split("..").let { it[0].toInt()..it[1].toInt() }
    val target = Target(xRange, yRange)

    val maxX = target.xRange.last
    val xValues = 0..maxX
    val yValues = xValues

    var bestHeight = 0
    var bestShot: Vector2? = null

    for (x in xValues) {
        for (y in yValues) {
            val initialState = State(Vector2(0, 0), Vector2(x, y))
            val generator = generateSequence(initialState) {
                simulate(it)
            }
            var maxReachedHeight = 0
            val finalState =
                generator
//                    .onEach { println(it) }
//                    .windowed(2)
//                    .takeWhile { !target.passedThrough(it[0].pos, it[1].pos) }
//                    .map { it.first() }
                    .takeWhile { it.pos.y >= target.yRange.first && it.pos.x <= target.xRange.last }
                    .onEach {
                        if (it.pos.y > maxReachedHeight) {
                            maxReachedHeight = it.pos.y
                        }
                    }.firstOrNull { target.contains(it.pos) }
            if (finalState != null && maxReachedHeight > bestHeight) {
                bestHeight = maxReachedHeight
                bestShot = Vector2(x, y)
            }
        }
    }

    return bestHeight
}

fun part2(input: List<String>): Int {
    val (xRangeString, yRangeString) = input.first().substringAfter(':').trim().split(", ")
    val xRange = xRangeString.split("=").last().split("..").let { it[0].toInt()..it[1].toInt() }
    val yRange = yRangeString.split("=").last().split("..").let { it[0].toInt()..it[1].toInt() }
    val target = Target(xRange, yRange)

    val maxX = target.xRange.last
    val xValues = 0..maxX
    val yValues = target.yRange.first..maxX

    var bestHeight = 0
    var count = 0
    var bestShot: Vector2? = null

    for (x in xValues) {
        for (y in yValues) {
            val initialState = State(Vector2(0, 0), Vector2(x, y))
            val generator = generateSequence(initialState) {
                simulate(it)
            }
            var maxReachedHeight = 0
            val finalState =
                generator
//                    .onEach { println(it) }
//                    .windowed(2)
//                    .takeWhile { !target.passedThrough(it[0].pos, it[1].pos) }
//                    .map { it.first() }
                    .takeWhile { it.pos.y >= target.yRange.first && it.pos.x <= target.xRange.last }
                    .onEach {
                        if (it.pos.y > maxReachedHeight) {
                            maxReachedHeight = it.pos.y
                        }
                    }.firstOrNull { target.contains(it.pos) }
            if (finalState != null) {
                count++
            }
            if (finalState != null && maxReachedHeight > bestHeight) {
                bestHeight = maxReachedHeight
                bestShot = Vector2(x, y)
            }
        }
    }

    return count
}

fun main() = AoCTask("day17").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    println(part1(input))
    println(part2(input))
}
