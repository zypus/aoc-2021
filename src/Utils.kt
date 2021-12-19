import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

data class AoCTask(val day: String) {

    /**
     * Reads lines from the given input txt file.
     */
    fun readInput(name: String) = File("src/$day", "$name.txt").readLines()

    val input by lazy {
        readInput(day.replaceFirstChar { it.uppercase() })
    }

    val testInput by lazy {
        readInput(day.replaceFirstChar { it.uppercase() } + "_test")
    }

    fun readTestInput(n: Int) = readInput(day.replaceFirstChar { it.uppercase() } + "_test$n")

}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

data class Vector2(val x: Int = 0, val y: Int = 0) {
    override fun toString(): String {
        return "($x,$y)"
    }
}

operator fun Vector2.plus(other: Vector2) = Vector2(x + other.x, y + other.y)