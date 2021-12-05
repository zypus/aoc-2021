package day05

import AoCTask
import Vector2

// https://adventofcode.com/2021/day/5

data class Line(val start: Vector2, val end: Vector2) {

    val xRange by lazy {
        if (start.x < end.x) start.x..end.x else start.x downTo end.x
    }
    val yRange by lazy {
        if (start.y < end.y) start.y..end.y else start.y downTo end.y
    }

    val isVertical = start.x == end.x
    val isHorizontal = start.y == end.y
    val isDiagonal = !(isVertical || isHorizontal)

    val coveredPoints: Set<Vector2> by lazy {
        when {
            isHorizontal -> xRange.map { Vector2(it, start.y) }
            isVertical -> yRange.map { Vector2(start.x, it) }
            isDiagonal -> xRange.zip(yRange).map { Vector2(it.first, it.second) }
            else -> throw Error("Invalid case, shouldn't happen")
        }.toSet()
    }

    fun intersections(other: Line): List<Vector2> {
        return when {
            isHorizontal && other.isVertical && other.start.x in xRange && start.y in other.yRange -> {
                listOf(Vector2(other.start.x, start.y))
            }
            isVertical && other.isHorizontal && start.x in other.xRange && other.start.y in yRange -> {
                listOf(Vector2(start.x, other.start.y))
            }
            isHorizontal && other.isHorizontal && start.y == other.start.y -> {
                xRange.toSet().intersect(other.xRange.toSet()).map { x ->
                    Vector2(x, start.y)
                }
            }
            isVertical && other.isVertical && start.x == other.start.x -> {
                yRange.toSet().intersect(other.yRange.toSet()).map { y ->
                    Vector2(start.x, y)
                }
            }
            // this case could of course handle all cases, but doing things explicitly yields more performance
            // skipping the other cases because I'm too lazy to think about the diagonal cases
            else -> return coveredPoints.intersect(other.coveredPoints).toList()
        }
    }
}

private fun inputToLines(
    input: List<String>,
    lineRegex: Regex = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()
) = input.map {
    val (x1, y1, x2, y2) = lineRegex.matchEntire(it)!!.destructured
    val start = Vector2(x1.toInt(), y1.toInt())
    val end = Vector2(x2.toInt(), y2.toInt())
    Line(start, end)
}

private fun findAllIntersections(lines: List<Line>) =
    lines.flatMapIndexed { index: Int, line: Line ->
        lines.drop(index + 1).flatMap { other ->
            line.intersections(other)
        }
    }.toSet()

fun part1(input: List<String>): Int {
    val lines = inputToLines(input)

    val filteredLines = lines.filter { it.isVertical || it.isHorizontal }

    val intersectionPoints = findAllIntersections(filteredLines)

    return intersectionPoints.size
}

fun part2(input: List<String>): Int {
    val lines = inputToLines(input)

    val intersectionPoints = findAllIntersections(lines)

    return intersectionPoints.size
}

fun main() = AoCTask("day05").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    println(part1(input))
    println(part2(input))
}
