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

}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
