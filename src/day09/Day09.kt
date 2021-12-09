package day09

import AoCTask

// https://adventofcode.com/2021/day/9

data class HeightMap(val heights: List<List<Int>>) {

    val width = heights[0].size
    val height = heights.size

    operator fun get(x: Int, y: Int): Int? {
        return if (x in 0 until width && y in 0 until height) {
            heights[y][x]
        } else {
            null
        }
    }

    fun isLowPoint(x: Int, y: Int): Boolean {
        val value = get(x, y)!!
        val neighbors = getNeighbors(x, y)
        return neighbors.mapNotNull { (nx, ny) -> get(nx, ny) }.all { value < it }
    }

    private fun getNeighbors(x: Int, y: Int) = listOf((x - 1) to y, (x + 1) to y, x to (y - 1), x to (y + 1))

    fun filterIndexed(block: (x: Int, y: Int, height: Int) -> Boolean): List<Int> {
        return heights.flatMapIndexed { y: Int, row: List<Int> -> row.mapIndexed { x, value -> Triple(x, y, value) } }.filter {
            block(it.first, it.second, it.third)
        }.map {
            it.third
        }
    }

    fun <T> mapIndexed(block: (x: Int, y: Int, height: Int) -> T): List<T> {
        return heights.flatMapIndexed { y: Int, row: List<Int> -> row.mapIndexed { x, value -> Triple(x, y, value) } }.map {
            block(it.first, it.second, it.third)
        }
    }

    fun floodFill(x: Int, y: Int, visited: List<Pair<Int, Int>> = listOf(x to y)): List<Pair<Int, Int>> {
        val value = get(x, y)!!
        val neighbors = getNeighbors(x, y).filter {
                (nx, ny) ->
            val nv = get(nx, ny)
            nv != null && nv != 9 && nv > value
        }.filter {
            it !in visited
        }
        var newList = visited + neighbors
        neighbors.forEach {
            newList = floodFill(it.first, it.second, newList)
        }
        return newList
    }

}

fun part1(input: List<String>): Int {
    val heights = input.map {
        it.map(Char::digitToInt)
    }
    val hMap = HeightMap(heights)
    val riskLevel = hMap.filterIndexed { x, y, _ ->
        hMap.isLowPoint(x, y)
    }.sumOf {
        it + 1
    }
    return riskLevel
}

fun part2(input: List<String>): Int {
    val heights = input.map {
        it.map(Char::digitToInt)
    }
    val hMap = HeightMap(heights)
    val basinScore = hMap.mapIndexed { x, y, _ ->
        if (hMap.isLowPoint(x, y)) {
            hMap.floodFill(x, y).size
        } else {
            null
        }
    }.filterNotNull()
        .sortedDescending()
        .take(3)
        .reduce(Int::times)

    return basinScore
}

fun main() = AoCTask("day09").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    println(part1(input))
    println(part2(input))
}
