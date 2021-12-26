package day20

import AoCTask
import Vector2
import plus

// https://adventofcode.com/2021/day/20


class LookupTable(val content: String) {

    operator fun get(index: Int): Boolean {
        return content[index] == '#'
    }

}

data class BinaryImage(val elements: Set<Vector2>, val lit: Boolean = true) {

    val minX = elements.minOf { it.x }
    val maxX = elements.maxOf { it.x }
    val minY = elements.minOf { it.y }
    val maxY = elements.maxOf { it.y }

    val width = maxX - minX
    val height = maxY - minY

    operator fun get(x: Int, y: Int): Boolean {
        return get(Vector2(x, y))
    }

    operator fun get(v: Vector2): Boolean {
        return if (lit) {
            v in elements
        } else {
            v !in elements
        }
    }

    val litElementCount = if (lit) elements.size else width * height - elements.size

    override fun toString(): String {
        val edge = 3
        return ((minY-edge)..(maxY+edge)).joinToString("\n") { y ->
            ((minX-edge)..(maxX+edge)).joinToString("") { x ->
                if (this[x, y]) "#" else "."
            }
        }
    }

    fun invert(): BinaryImage {
        val inverted = ((minY)..(maxY)).flatMap { y ->
            ((minX)..(maxX)).mapNotNull { x ->
                if (lit && !this[x, y]) {
                    Vector2(x, y)
                } else if (!lit && !this[x, y]) {
                    Vector2(x, y)
                } else {
                    null
                }
            }
        }.toSet()
        return BinaryImage(inverted, !lit)
    }

}

fun parseInput(input: List<String>): Pair<LookupTable, BinaryImage> {
    val table = LookupTable(input.first())
    val litElements = input.drop(2).flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') {
                Vector2(x, y)
            } else {
                null
            }
        }
    }.toSet()
    val image = BinaryImage(litElements)
    return table to image
}

fun createEnhancementSequence(image: BinaryImage, table: LookupTable): Sequence<BinaryImage> {
    val kernel = listOf(
        Vector2(-1, -1), Vector2(0, -1), Vector2(1, -1),
        Vector2(-1, 0), Vector2(0,0), Vector2(1, 0),
        Vector2(-1, 1), Vector2(0, 1), Vector2(1, 1))
    return generateSequence(image) { img ->
        val newLitElements = mutableSetOf<Vector2>()
        for (x in (img.minX-1)..(img.maxX+1)) {
            for (y in (img.minY-1)..(img.maxY+1)) {
                val pixel = Vector2(x, y)
                val binary9Bit = kernel.joinToString("") { dir ->
                    if (img[pixel + dir]) "1" else "0"
                }
                val index = binary9Bit.toInt(2)
                if (table[index]) {
                    newLitElements.add(pixel)
                }
            }
        }
        val edgePos = Vector2(img.maxX+10, img.minY+10)
        val edgeBinary9Bit = kernel.joinToString("") { dir ->
            if (img[edgePos + dir]) "1" else "0"
        }
        val edgeValue = table[edgeBinary9Bit.toInt(2)]
        val newImage = BinaryImage(newLitElements)
        if (edgeValue != newImage[edgePos]) {
            newImage.invert()
        } else {
            newImage
        }
    }
}

fun part1(input: List<String>): Int {
    val (lookupTable, binaryImage) = parseInput(input)
    val sequence = createEnhancementSequence(binaryImage, lookupTable)
    val result = sequence.take(1 + 2).last()
    return result.litElementCount
}

fun part2(input: List<String>): Int {
    val (lookupTable, binaryImage) = parseInput(input)
    val sequence = createEnhancementSequence(binaryImage, lookupTable)
    val result = sequence.take(1 + 50).last()
    return result.litElementCount
}

fun main() = AoCTask("day20").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    println(part1(input))
    println(part2(input))
}