package day18

import AoCTask
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

// https://adventofcode.com/2021/day/18

sealed class SnailfishNumber {

    data class Regular(val value: Int): SnailfishNumber() {

        override fun toString(): String {
            return value.toString()
        }
    }

    data class Pair(val left: SnailfishNumber, val right: SnailfishNumber): SnailfishNumber() {

        override fun toString(): String {
            return "[$left,$right]"
        }
    }

    fun addToLeft(value: Int): SnailfishNumber {
        return when(this) {
            is Pair -> left.addToLeft(value) tos right
            is Regular -> Regular(this.value + value)
        }
    }

    fun addToRight(value: Int): SnailfishNumber {
        return when(this) {
            is Pair -> left tos right.addToRight(value)
            is Regular -> Regular(this.value + value)
        }
    }

    companion object {

        fun parse(input: String): SnailfishNumber {
            val stack = Stack<SnailfishNumber>()
            input.forEach { c ->
                when {
                    c in listOf('[', ',') -> {}
                    c == ']' -> {
                        val right = stack.pop()
                        val left = stack.pop()
                        stack.push(Pair(left, right))
                    }
                    c.isDigit() -> {
                        stack.push(Regular(c.digitToInt()))
                    }
                    else -> throw Error("Unexpected character $c")
                }
            }
            return stack.pop()
        }

    }

}

operator fun SnailfishNumber.plus(other: SnailfishNumber): SnailfishNumber {
    return (this tos other).reduce()
}

fun Int.toSnail() = SnailfishNumber.Regular(this)
infix fun Int.tos(right: Int) = SnailfishNumber.Pair(this.toSnail(), right.toSnail())
infix fun Int.tos(right: SnailfishNumber) = SnailfishNumber.Pair(this.toSnail(), right)
infix fun SnailfishNumber.tos(right: Int) = SnailfishNumber.Pair(this, right.toSnail())
infix fun SnailfishNumber.tos(right: SnailfishNumber) = SnailfishNumber.Pair(this, right)

fun String.toSnail(): SnailfishNumber = SnailfishNumber.parse(this)

sealed class ExplosionResult {

    abstract val value: SnailfishNumber

    data class Explosion(override val value: SnailfishNumber, val explosion: Pair<Int, Int>): ExplosionResult()
    data class NoExplosion(override val value: SnailfishNumber): ExplosionResult()
}

fun SnailfishNumber.reduce(): SnailfishNumber {
    var currentNumber = this
    var reduce = true
    while (reduce) {
        reduce = false
        val explosionResult = currentNumber.explode()
        if (explosionResult is ExplosionResult.Explosion) {
            currentNumber = explosionResult.value
            reduce = true
        } else {
            val splitResult = currentNumber.split()
            if (splitResult is SplitResult.Split) {
                currentNumber = splitResult.value
                reduce = true
            }
        }
    }
    return currentNumber
}

fun SnailfishNumber.explode(depth: Int = 0): ExplosionResult {
    return when(this) {
        is SnailfishNumber.Pair -> {
            if (depth == 4) {
                ExplosionResult.Explosion(0.toSnail(), (left as SnailfishNumber.Regular).value to (right as SnailfishNumber.Regular).value)
            } else {
                val leftExplosion = left.explode(depth + 1)
                if (leftExplosion is ExplosionResult.Explosion) {
                    when(right) {
                        is SnailfishNumber.Pair -> ExplosionResult.Explosion(leftExplosion.value tos right.addToLeft(leftExplosion.explosion.second), leftExplosion.explosion.first to 0)
                        is SnailfishNumber.Regular -> ExplosionResult.Explosion(leftExplosion.value tos right.value + leftExplosion.explosion.second, leftExplosion.explosion.first to 0)
                    }
                } else {
                   val rightExplosion = right.explode(depth + 1)
                    if (rightExplosion is ExplosionResult.Explosion) {
                        when(left) {
                            is SnailfishNumber.Pair -> ExplosionResult.Explosion(left.addToRight(rightExplosion.explosion.first) tos rightExplosion.value, 0 to rightExplosion.explosion.second)
                            is SnailfishNumber.Regular -> ExplosionResult.Explosion(rightExplosion.explosion.first + left.value tos rightExplosion.value,  0 to rightExplosion.explosion.second)
                        }
                    } else {
                        ExplosionResult.NoExplosion(this)
                    }
                }
            }
        }
        is SnailfishNumber.Regular -> ExplosionResult.NoExplosion(this)
    }
}

sealed class SplitResult() {

    abstract val value: SnailfishNumber

    data class Split(override val value: SnailfishNumber): SplitResult()
    data class NoSplit(override val value: SnailfishNumber): SplitResult()
}

fun SnailfishNumber.split(): SplitResult {
    return when(this) {
        is SnailfishNumber.Pair -> {
            val leftSplit = left.split()
            if (leftSplit is SplitResult.Split) {
                SplitResult.Split(leftSplit.value tos right)
            } else {
                val rightSplit = right.split()
                if (rightSplit is SplitResult.Split) {
                    SplitResult.Split(left tos rightSplit.value)
                } else {
                    SplitResult.NoSplit(this)
                }
            }
        }
        is SnailfishNumber.Regular -> {
            if (this.value > 9) {
                val half = (this.value.toDouble() / 2)
                SplitResult.Split(floor(half).toInt() tos ceil(half).toInt())
            } else {
                SplitResult.NoSplit(this)
            }
        }
    }
}

val SnailfishNumber.magnitude: Int get() = when(this) {
    is SnailfishNumber.Pair -> 3 * left.magnitude + 2 * right.magnitude
    is SnailfishNumber.Regular -> value
}

fun part1(input: List<String>): Int {
    return input.map(String::toSnail).reduce(SnailfishNumber::plus).magnitude
}

fun part2(input: List<String>): Int {
    val numbers = input.map(String::toSnail)
    val combinations = numbers.flatMapIndexed { i, first ->
        numbers.flatMapIndexed { j, second ->
            if (i == j) {
                listOf()
            } else {
                listOf(first to second, second to first)
            }
        }
    }
    val maxMagnitude = combinations.maxOf {
        (it.first + it.second).magnitude
    }
    return maxMagnitude
}

fun main() = AoCTask("day18").run {
    testParsing()
    testExplosion()
    testAddition()
    testMagnitude()
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    println(part1(input))
    println(part2(input))
}

fun testParsing() {
    check(SnailfishNumber.parse("[1,2]") == (1 tos 2))
    check(SnailfishNumber.parse("[[1,2],3]") == (1 tos 2) tos 3)
    check(SnailfishNumber.parse("[9,[8,7]]") == 9 tos (8 tos 7))
    check(SnailfishNumber.parse("[[1,9],[8,5]]") == (1 tos 9) tos (8 tos 5))
    check(SnailfishNumber.parse("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]") == (((1 tos 2) tos (3 tos 4)) tos ((5 tos 6) tos (7 tos 8))) tos 9)
}

fun testExplosion() {
    check(SnailfishNumber.parse("[[[[[9,8],1],2],3],4]").explode().value == SnailfishNumber.parse("[[[[0,9],2],3],4]"))
    check(SnailfishNumber.parse("[7,[6,[5,[4,[3,2]]]]]").explode().value == SnailfishNumber.parse("[7,[6,[5,[7,0]]]]"))
    check(SnailfishNumber.parse("[[6,[5,[4,[3,2]]]],1]").explode().value == SnailfishNumber.parse("[[6,[5,[7,0]]],3]"))
    check(SnailfishNumber.parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").explode().value == SnailfishNumber.parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"))
    check(SnailfishNumber.parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]").explode().value == SnailfishNumber.parse("[[3,[2,[8,0]]],[9,[5,[7,0]]]]"))
}

fun testAddition() {
    check("[[[[4,3],4],4],[7,[[8,4],9]]]".toSnail() + "[1,1]".toSnail() == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".toSnail())
}

fun testMagnitude() {
    check("[[1,2],[[3,4],5]]".toSnail().magnitude == 143)
    check("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".toSnail().magnitude == 1384)
    check("[[[[1,1],[2,2]],[3,3]],[4,4]]".toSnail().magnitude == 445)
    check("[[[[3,0],[5,3]],[4,4]],[5,5]]".toSnail().magnitude == 791)
    check("[[[[5,0],[7,4]],[5,5]],[6,6]]".toSnail().magnitude == 1137)
    check("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]".toSnail().magnitude == 3488)
}
