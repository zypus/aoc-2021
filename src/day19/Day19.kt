package day19

import AoCTask
import Vector3
import minus
import permutations
import plus
import squaredLength
import unaryMinus
import kotlin.math.abs
import kotlin.random.Random

// https://adventofcode.com/2021/day/19

data class Scanner(val id: Int, val beacons: List<Beacon>)

data class Beacon(val relPos: Vector3)

fun Beacon.squaredDistanceTo(other: Beacon) = (other.relPos - relPos).squaredLength()

fun Scanner.fingerPrint(): List<Pair<Beacon, List<Long>>> {
    return beacons.mapIndexed { i, beacon ->
        beacon to beacons.filterIndexed { j, _ -> i != j }.map { other ->
            beacon.squaredDistanceTo(other)
        }
    }
}

fun Scanner.overlap(other: Scanner): List<Pair<Beacon, Beacon>> {
    val fingerPrint = this.fingerPrint()
    val otherFingerPrint = other.fingerPrint()
    val overlap = mutableListOf<Pair<Beacon, Beacon>>()
    val beaconCandidates = fingerPrint.forEach { (beacon, beaconPrint) ->
        val match = otherFingerPrint.find { (_, otherBeaconPrint) ->
            val mutBeaconPrint = otherBeaconPrint.toMutableList()
            beaconPrint.count {
                if (it in mutBeaconPrint) {
                    mutBeaconPrint.remove(it)
                    true
                } else {
                    false
                }
            } >= 11
        }
        if (match != null) {
            overlap.add(beacon to match.first)
        }
    }
    return overlap
}

fun parseScanners(input: List<String>): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var currentId = 0
    val currentBeacons = mutableListOf<Beacon>()
    input.forEach { line ->
        if ("scanner" in line) {
            currentId = line.split(" ")[2].toInt()
        } else if (line.isEmpty()) {
            scanners.add(Scanner(currentId, currentBeacons.toList()))
            currentBeacons.clear()
        } else {
            val (x, y, z) = line.split(",")
            currentBeacons.add(Beacon(Vector3(x.toInt(), y.toInt(), z.toInt())))
        }
    }
    scanners.add(Scanner(currentId, currentBeacons.toList()))
    return scanners
}


fun allSignFlips(orientation: List<String>): List<List<String>> {
    return if (orientation.size == 1) {
        val element = orientation.first()
        listOf(listOf(element), listOf("-$element"))
    } else {
        val element = orientation.first()
        val remaining = orientation.drop(1)
        allSignFlips(remaining).flatMap { flipped ->
            listOf(listOf(element) + flipped, listOf("-$element") + flipped)
        }
    }
}

data class CoordinateSystem(val first: String, val second: String, val third: String) {

    val all = listOf(first, second, third)

    val firstIndex = index(first)
    val secondIndex = index(second)
    val thirdIndex = index(third)

    val firstSign = sign(first)
    val secondSign = sign(second)
    val thirdSign = sign(third)

    val rightHandedSystem: Boolean

    init {
        val all = listOf(first, second, third)
        val x = all.first { "x" in it }
        val y = all.first { "y" in it }
        val z = all.first { "z" in it }

        val sameSign = sign(x) == sign(y)

        val signZ = sign(z)

        rightHandedSystem = (sameSign && signZ > 0) || (sameSign && signZ < 0)
    }

    fun convert(vector: Vector3): Vector3 {
        val elements = vector.elements
        val x = elements[firstIndex] * firstSign
        val y = elements[secondIndex] * secondSign
        val z = elements[thirdIndex] * thirdSign

        return Vector3(x, y, z)
    }

    fun index(string: String): Int {
        return when {
            "x" in string -> 0
            "y" in string -> 1
            "z" in string -> 2
            else -> throw Error("Invalid '$string'")
        }
    }

    fun inverseIndex(index: Int): Int {
        return when(index) {
            0 -> all.indexOfFirst { "x" in it }
            1 -> all.indexOfFirst { "y" in it }
            2 -> all.indexOfFirst { "z" in it }
            else -> throw Error("Invalid '$index'")
        }
    }

    fun sign(string: String): Int {
        return if ("-" in string) {
            -1
        } else {
            1
        }
    }

    override fun toString(): String {
        return "[$first,$second,$third]"
    }

}

data class ScannerPair(val from: Scanner, val other: Scanner, val coordinateSystem: CoordinateSystem, val otherPosRelativeToFrom: Vector3, val inverseCoordinateSystem: CoordinateSystem, val fromPosRelativeToOther: Vector3) {
    override fun toString(): String {
        return "${from.id} -> ${other.id} $otherPosRelativeToFrom $coordinateSystem"
    }

    fun invert(): ScannerPair {
        return ScannerPair(other, from, inverseCoordinateSystem, fromPosRelativeToOther, coordinateSystem, otherPosRelativeToFrom)
    }
}

data class ScannerChain(val end: Scanner, val path: List<ScannerPair>) {
    override fun toString(): String {
        return "${end.id} <- ${path.reversed().joinToString(" <- ") { it.from.id.toString() }}"
    }
}

fun part1(input: List<String>): Int {
    val orientations = listOf("x", "y", "z").permutations().flatMap { allSignFlips(it) }.distinct().map {
        CoordinateSystem(it[0], it[1], it[2])
    }

    val scanners = parseScanners(input)
    val scannerPairs = constructScannerPairs(scanners, orientations)
    val scannerChains = constructScannerChains(scanners, scannerPairs)

    val allBeacons = scannerChains.flatMap { chain ->
        if (chain.path.isEmpty()) {
            chain.end.beacons
        } else {
            chain.path.reversed().fold(chain.end.beacons.map { it.relPos }) { beacons, pair ->
                beacons.map { beacon ->
                    pair.otherPosRelativeToFrom + pair.coordinateSystem.convert(beacon)
                }
            }.map { pos ->
                Beacon(pos)
            }
        }
    }.distinct()

//    allBeacons.sortedBy { it.relPos.x }.forEach {
//        println(it.relPos.toString().trim('(', ')'))
//    }

    return allBeacons.size
}

private fun constructScannerChains(
    scanners: List<Scanner>,
    scannerPairs: List<ScannerPair>
): MutableList<ScannerChain> {
    val knownScanners = mutableListOf(scanners.first())
    val unknownScanners = scanners.drop(1).toMutableList()
    val openPairs = scannerPairs.toMutableList()

    val scannerChains = mutableListOf(ScannerChain(scanners.first(), emptyList()))

    while (unknownScanners.isNotEmpty()) {
        val nextPair = openPairs.first {
            it.from in knownScanners && it.other in unknownScanners || it.other in knownScanners && it.from in unknownScanners
        }
        val orientedPair = if (nextPair.from in knownScanners) {
            nextPair
        } else {
            nextPair.invert()
        }

        val chain = scannerChains.first { it.end == orientedPair.from }
        scannerChains.add(ScannerChain(orientedPair.other, chain.path + orientedPair))

        knownScanners.add(orientedPair.other)
        unknownScanners.remove(orientedPair.other)
        openPairs.remove(nextPair)
    }
    return scannerChains
}

private fun constructScannerPairs(
    scanners: List<Scanner>,
    orientations: List<CoordinateSystem>
): List<ScannerPair> {
    val scannerPairs = scanners.flatMapIndexed { index, scanner ->
        scanners.drop(index + 1).mapNotNull { other ->
            val overlap = scanner.overlap(other)
            if (overlap.size > 11) {
//                println("Scanner ${scanner.id} overlaps with ${other.id}")
                val usedSystem = orientations.single { system ->
                    val otherScannerCoords = overlap.map { (left, right) ->
                        left.relPos + (-(system.convert(right.relPos)))
                    }
                    otherScannerCoords.all { it == otherScannerCoords.first() }
                }
                val inverseSystem = orientations.single { system ->
                    val otherScannerCoords = overlap.map { (left, right) ->
                        right.relPos + (-(system.convert(left.relPos)))
                    }
                    otherScannerCoords.all { it == otherScannerCoords.first() }
                }
//                println("$usedSystem $inverseSystem")
                val pos = overlap.first().first.relPos + (-(usedSystem.convert(overlap.first().second.relPos)))
                val inversePos =
                    overlap.first().second.relPos + (-(inverseSystem.convert(overlap.first().first.relPos)))
                ScannerPair(scanner, other, usedSystem, pos, inverseSystem, inversePos)
            } else {
                null
            }
        }
    }
    return scannerPairs
}

fun part2(input: List<String>): Int {
    val orientations = listOf("x", "y", "z").permutations().flatMap { allSignFlips(it) }.distinct().map {
        CoordinateSystem(it[0], it[1], it[2])
    }

    val scanners = parseScanners(input)
    val scannerPairs = constructScannerPairs(scanners, orientations)
    val scannerChains = constructScannerChains(scanners, scannerPairs)

    val scannerPositions = scannerChains.map { chain ->
        if (chain.path.isEmpty()) {
            Vector3(0,0,0)
        } else {
            chain.path.reversed().fold(Vector3(0,0,0)) { pos, pair ->
                pair.otherPosRelativeToFrom + pair.coordinateSystem.convert(pos)
            }
        }
    }

//    scannerPositions.forEach {
//        println(it)
//    }

    val maxManhatten = scannerPositions.flatMap { pos ->
        scannerPositions.map { other ->
            other - pos
        }
    }.maxOf {
        abs(it.x) + abs(it.y) + abs(it.z)
    }

    return maxManhatten
}

fun main() = AoCTask("day19").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    println(part1(input))
    println(part2(input))
}
