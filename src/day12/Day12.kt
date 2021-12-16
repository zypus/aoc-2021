package day12

import AoCTask

// https://adventofcode.com/2021/day/12

enum class SIZE {
    SMALL, LARGE
}

data class Node(val name: String, val size: SIZE) {
    constructor(name: String): this(name, if(name.first().isUpperCase()) SIZE.LARGE else SIZE.SMALL)
}

data class Graph(val nodes: List<Node>, val edges: List<Pair<Node, Node>>) {

    fun neighborsOf(node: Node): List<Node> {
        return edges.filter {
            it.first == node || it.second == node
        }.map {
            if (it.first == node) it.second else it.first
        }
    }

    fun allPaths(startNode: Node, endNode: Node): List<List<Node>> {
        return findPaths(listOf(startNode), endNode)
    }

    fun findPaths(currentPath: List<Node>, endNode: Node): List<List<Node>> {
        val currentNode = currentPath.last()
        return if (currentNode == endNode) {
            listOf(currentPath)
        } else {
            neighborsOf(currentNode).filter { nextNode ->
                nextNode.size == SIZE.LARGE || nextNode !in currentPath
            }.flatMap { nextNode ->
                findPaths(currentPath + nextNode, endNode)
            }
        }
    }

}

fun part1(input: List<String>): Int {
    val nodes = mutableMapOf<String, Node>()
    val edges = input.map {
        val (from, to) = it.split("-")
        val fromNode = nodes.getOrPut(from) {
            Node(from)
        }
        val toNode = nodes.getOrPut(to) {
            Node(to)
        }
        fromNode to toNode
    }
    val graph = Graph(nodes.values.toList(), edges)

    return graph.allPaths(nodes["start"]!!, nodes["end"]!!).size
}

fun part2(input: List<String>): Int {
    return input.size
}

fun main() = AoCTask("day12").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 10)
    check(part1(readTestInput(2)) == 19)
    check(part1(readTestInput(3)) == 226)

    println(part1(input))
    println(part2(input))
}
