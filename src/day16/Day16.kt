package day16

import AoCTask

// https://adventofcode.com/2021/day/16

enum class OpCode(val code: Int) {
    SUM(0),
    PRODUCT(1),
    MINIMUM(2),
    MAXIMUM(3),
    GREATER_THAN(5),
    LESS_THAN(6),
    EQUAL_TO(7),
}

sealed class Packet() {
    abstract val version: Int

    data class Literal(override val version: Int, val value: Long) : Packet()
    data class Operator(override val version: Int, val opCode: OpCode, val subPackages: List<Packet>) : Packet()

}

fun Packet.versionSum(): Int = when(this) {
    is Packet.Literal -> version
    is Packet.Operator -> version + subPackages.sumOf { it.versionSum() }
}

fun Packet.compute(): Long = when(this) {
    is Packet.Literal -> value
    is Packet.Operator -> {
        val computedSubpackages = subPackages.map { it.compute() }
        when(opCode) {
            OpCode.SUM -> computedSubpackages.sum()
            OpCode.PRODUCT -> computedSubpackages.takeIf { it.size > 1 }?.reduce(Long::times) ?: computedSubpackages.first()
            OpCode.MINIMUM -> computedSubpackages.minOrNull()!!
            OpCode.MAXIMUM -> computedSubpackages.maxOrNull()!!
            OpCode.GREATER_THAN -> if (computedSubpackages[0] > computedSubpackages[1]) 1 else 0
            OpCode.LESS_THAN -> if (computedSubpackages[0] < computedSubpackages[1]) 1 else 0
            OpCode.EQUAL_TO -> if (computedSubpackages[0] == computedSubpackages[1]) 1 else 0
        }
    }
}

fun parsePacket(input: String): Pair<Packet, String> {
    val version = input.take(3).toInt(2)
    val type = input.substring(3).take(3).toInt(2)
    val remainingInput = input.substring(6)
    return when (type) {
        4 -> {
            val (binaryLiteral, remaining) = parseLiteral(remainingInput)
           Packet.Literal(version, binaryLiteral.toLong(2)) to remaining
        }
        else -> {
            val (subPackages, remaining) = parseSubpackages(remainingInput)
            Packet.Operator(version, OpCode.values().first { it.code == type }, subPackages) to remaining
        }
    }
}

fun parseLiteral(input: String, currentBinaryLiteral: String = ""): Pair<String, String> {
    val block = input.take(5)
    val remainingInput = input.substring(5)
    val binaryLiteral = currentBinaryLiteral + block.drop(1)
    return if (block.startsWith('1')) {
        parseLiteral(remainingInput, binaryLiteral)
    } else {
        binaryLiteral to remainingInput
    }
}

fun parseSubpackages(input: String): Pair<List<Packet>, String> {
    val lengthType = input.take(1).toInt(2)
    return if (lengthType == 0) {
        val length = input.substring(1).take(15).toInt(2)
        var subPackageData = input.substring(16).take(length)
        val subPackages = mutableListOf<Packet>()
        while (subPackageData.isNotEmpty()) {
            val (subPacket, remaining) = parsePacket(subPackageData)
            subPackageData = remaining
            subPackages.add(subPacket)
        }
        subPackages to input.substring(16 + length)
    } else {
        val count = input.substring(1).take(11).toInt(2)
        var remainingInput = input.substring(12)
        val subPackages = mutableListOf<Packet>()
        for (c in 0 until count) {
            val (subPacket, remaining) = parsePacket(remainingInput)
            remainingInput = remaining
            subPackages.add(subPacket)
        }
        subPackages to remainingInput
    }
}

private fun convertToBinaryString(input: List<String>) =
    input.first().map { it.digitToInt(16) }.joinToString("") { it.toString(2).padStart(4, '0') }

fun part1(input: List<String>): Int {
    val binaryInput = convertToBinaryString(input)
    val (packet, _) = parsePacket(binaryInput)
    return packet.versionSum()
}

fun part2(input: List<String>): Long {
    val binaryInput = convertToBinaryString(input)
    val (packet, _) = parsePacket(binaryInput)
    return packet.compute()
}

fun main() = AoCTask("day16").run {
    // test if implementation meets criteria from the description, like:
    testInput.asSequence().onEach { println("Checking: $it") }.map { it.split(" -> ") }.forEach { pair ->
        check(part1(listOf(pair[0])) == pair[1].toInt())
    }
    readTestInput(2).asSequence().onEach { println("Checking: $it") }.map { it.split(" -> ") }.forEach { pair ->
        check(part2(listOf(pair[0])) == pair[1].toLong())
    }

    println(part1(input))
    println(part2(input))
}
