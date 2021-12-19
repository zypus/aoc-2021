package day13

import AoCTask
import Vector2

// https://adventofcode.com/2021/day/13

sealed class Fold(val pos: Int) {
    data class Horizontal(val y: Int) : Fold(y) {
        override fun Vector2.accessor(): Int = y
        override fun Vector2.update(p: Int) = copy(y=p)
    }

    data class Vertical(val x: Int) : Fold(x) {
        override fun Vector2.accessor(): Int = x
        override fun Vector2.update(p: Int) = copy(x=p)
    }

    abstract fun Vector2.accessor() : Int
    abstract fun Vector2.update(p: Int) : Vector2

    fun fold(page: Page): Page {
        val (beforeFold, afterFold) = page.dots.partition { dot -> dot.accessor() < this.pos }
        val folded = afterFold.map { dot ->
            dot.update(this.pos - (dot.accessor() - this.pos))
        }
        return Page((beforeFold + folded).distinct())
    }

}

data class Page(val dots: List<Vector2>){
    override fun toString(): String {
        val maxX = dots.maxOf { it.x }
        val maxY = dots.maxOf { it.y }
        return (0..maxY).joinToString("\n") { y ->
            val line = dots.filter { it.y == y }
            (0..maxX).joinToString("") { x ->
                if (line.any { it.x == x }) "#" else "."
            }
        }
    }
}

fun parseIntoPageAndFolds(input: List<String>): Pair<Page, List<Fold>> {
    val dots = input.takeWhile { it.isNotEmpty() }.map {
        val (x, y) = it.split(",")
        Vector2(x.toInt(), y.toInt())
    }
    val folds = input.filter { it.startsWith("fold") }.map {
        val (fold, pos) = it.split("=")
        if ("x" in fold) {
            Fold.Vertical(pos.toInt())
        } else {
            Fold.Horizontal(pos.toInt())
        }
    }
    return Page(dots) to folds
}

fun part1(input: List<String>): Int {
    val (page, folds) = parseIntoPageAndFolds(input)

    return folds.first().fold(page).dots.size
}

fun part2(input: List<String>): Page {
    val (page, folds) = parseIntoPageAndFolds(input)

    return folds.fold(page) { acc, fold -> fold.fold(acc)}
}

fun main() = AoCTask("day13").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 17)

    println(part1(input))
    println(part2(input))
}
