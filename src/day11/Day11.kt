package day11

import AoCTask
import Vector2

// https://adventofcode.com/2021/day/11

data class Octopus(val energy: Int)

data class OctopusGrid(val rows: List<List<Octopus>>, val flashes: Int = 0) {

    private val width by lazy { rows.first().size }
    private val height by lazy { rows.size }

    fun step(): OctopusGrid {
        val energized = this.rows.map { row ->
            row.map { it.copy(energy = it.energy + 1) }
        }
        val triggeredOct = mutableListOf<Vector2>()
        var updated = flash(energized, triggeredOct)
        var totalFlashed = updated.flashes
        while (triggeredOct.isNotEmpty()) {
            updated = energize(updated.rows, triggeredOct)
            triggeredOct.clear()
            updated = flash(updated.rows, triggeredOct)
            totalFlashed += updated.flashes
        }
        return updated.copy(flashes = totalFlashed)
    }

    private fun energize(
        updated: List<List<Octopus>>,
        triggeredOct: MutableList<Vector2>
    ): OctopusGrid {
        val newGrid =  updated.mapIndexed { y, row ->
            row.mapIndexed { x, oct ->
                if (oct.energy > 0) {
                    val triggerCount = triggeredOct.count { it.x == x && it.y == y }
                    if (triggerCount > 0) {
                        oct.copy(energy = oct.energy + triggerCount)
                    } else {
                        oct
                    }
                } else {
                    oct
                }
            }
        }
        return OctopusGrid(newGrid)
    }

    private fun flash(
        energized: List<List<Octopus>>,
        triggeredOct: MutableList<Vector2>
    ): OctopusGrid {
        var flashes = 0
        val newGrid = energized.mapIndexed { y, row ->
            row.mapIndexed { x, oct ->
                if (oct.energy > 9) {
                    flashes++
                    triggeredOct.addAll(validNeighbourCoords(x, y))
                    oct.copy(energy = 0)
                } else {
                    oct
                }
            }
        }

        return OctopusGrid(newGrid, flashes)
    }

    private fun validNeighbourCoords(x: Int, y: Int): List<Vector2> {
        val candidates = listOf(-1, 0, 1).flatMap { dx -> listOf(-1, 0, 1).map { dy -> Vector2((x + dx), (y + dy)) } }
        return candidates.filter {
            it.x in 0 until width && it.y in 0 until height
        }
    }

    operator fun get(x: Int, y: Int): Octopus? {
        return if (x in 0 until width && y in 0 until height) {
            rows[x][y]
        } else {
            null
        }
    }

    override fun toString(): String {
        return rows.joinToString("\n") { row ->
            row.joinToString("") { oct ->
                if (oct.energy == 0) {
                    "\u001B[1m\u001B[32m${oct.energy}\u001B[0m"
                } else {
                    oct.energy.toString()
                }
            }
        }
    }
}


fun part1(input: List<String>): Int {
    val grid = OctopusGrid(input.map { line ->
        line.map { Octopus(it.digitToInt()) }
    })
    val gridSequence = generateSequence(grid) {
        it.step()
    }
    return gridSequence.take(1 + 100).sumOf { it.flashes }
}

fun part2(input: List<String>): Int {
    val grid = OctopusGrid(input.map { line ->
        line.map { Octopus(it.digitToInt()) }
    })
    val gridSequence = generateSequence(grid) {
        it.step()
    }
    return gridSequence.takeWhile { g ->
        !g.rows.all { row -> row.all { it.energy == 0 } }
    }.count()
}

fun main() = AoCTask("day11").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    println(part1(input))
    println(part2(input))
}
