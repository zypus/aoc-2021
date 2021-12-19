package day15

import AoCTask
import Vector2
import minus
import plus
import java.util.*
import kotlin.math.abs
import kotlin.math.max

// https://adventofcode.com/2021/day/15

data class CostMatrix(val cells: List<List<Int>>) {

    val width: Int by lazy {
        cells.first().size
    }
    val height: Int by lazy {
        cells.size
    }

    companion object {
        fun parse(input: List<String>): CostMatrix {
            return CostMatrix(input.map { line -> line.map(Char::digitToInt) })
        }
    }

    operator fun get(x: Int, y: Int): Int? {
        return if (x in 0 until width && y in 0 until height) {
            cells[y][x]
        } else {
            null
        }
    }

}

fun part1(input: List<String>): Int {
    val riskMatrix = CostMatrix.parse(input)
    val costFunction = { node: Vector2 -> riskMatrix[node.x, node.y] ?: Int.MAX_VALUE }
    val directions = listOf(Vector2.UP, Vector2.DOWN, Vector2.LEFT, Vector2.RIGHT)
    val neighbors = { node: Vector2 ->
        directions.map { dir -> dir + node }
            .filter { it.x in 0 until riskMatrix.width && it.y in 0 until riskMatrix.height }
    }
    val start = Vector2(0, 0)
    val goal = Vector2(riskMatrix.width - 1, riskMatrix.height - 1)
    val bestPath = aStar(start, goal, neighbors, costFunction) {
        val delta = goal - it
        abs(delta.x) + abs(delta.y)
    }
    return bestPath?.let { path -> path.drop(1).sumOf { riskMatrix[it.x, it.y]!! } } ?: 0
}

fun part2(input: List<String>): Int {
    val riskMatrix = CostMatrix.parse(input)
    val costFunction = { node: Vector2 ->
        val widthRiskIncrease = node.x / riskMatrix.width
        val heightRiskIncrease = node.y / riskMatrix.height
        val riskIncrease = widthRiskIncrease + heightRiskIncrease
        val risk = riskMatrix[node.x % riskMatrix.width, node.y % riskMatrix.height]!!
        val newRisk = risk + riskIncrease
        if (newRisk > 9) {
            newRisk - 9
        } else {
            newRisk
        }
    }

    val directions = listOf(Vector2.UP, Vector2.DOWN, Vector2.LEFT, Vector2.RIGHT)
    val neighbors = { node: Vector2 ->
        directions.map { dir -> dir + node }
            .filter { it.x in 0 until (riskMatrix.width * 5) && it.y in 0 until (riskMatrix.height * 5) }
    }
    val start = Vector2(0, 0)
    val goal = Vector2(5 * riskMatrix.width - 1, 5 * riskMatrix.height - 1)
    val bestPath = aStar(start, goal, neighbors, costFunction) {
        val delta = goal - it
        abs(delta.x) + abs(delta.y)
    }
    return bestPath?.let { path -> path.drop(1).sumOf { costFunction(it) } } ?: 0
}

fun aStar(
    start: Vector2,
    goal: Vector2,
    neighbors: (node: Vector2) -> List<Vector2>,
    cost: (node: Vector2) -> Int,
    heuristic: (node: Vector2) -> Int
): List<Vector2>? {

    val cheapestPathTo = mutableMapOf<Vector2, Int>().withDefault { Int.MAX_VALUE }
    cheapestPathTo[start] = 0
    val bestGuess = mutableMapOf<Vector2, Int>().withDefault { Int.MAX_VALUE }
    bestGuess[start] = 0

    val openNodes = PriorityQueue<Vector2>(1) { a, b ->
        bestGuess.getValue(a) - bestGuess.getValue(b)
    }
    openNodes.add(start)

    val cameFrom = mutableMapOf<Vector2, Vector2>()

    while (openNodes.isNotEmpty()) {
        val current = openNodes.remove()
        if (current == goal) {
            var path = listOf(current)
            var next = current
            while (next in cameFrom) {
                next = cameFrom.getValue(next)
                path = path + next
            }
            return path.reversed()
        } else {
            neighbors(current).forEach { neighbor ->
                val tentativeScore = cheapestPathTo.getValue(current) + cost(neighbor)
                if (tentativeScore < cheapestPathTo.getValue(neighbor)) {
                    cameFrom[neighbor] = current
                    cheapestPathTo[neighbor] = tentativeScore
                    bestGuess[neighbor] = tentativeScore + heuristic(neighbor)
                    if (neighbor !in openNodes) {
                        openNodes.add(neighbor)
                    }
                }
            }
        }
    }

    return null
}

fun main() = AoCTask("day15").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    println(part1(input))
    println(part2(input))
}
