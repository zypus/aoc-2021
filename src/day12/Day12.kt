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

    fun findPathsAllowDoubleVisit(currentPath: List<Node>, endNode: Node, doubleVisitNode: Node): List<List<Node>> {
        val currentNode = currentPath.last()
        return if (currentNode == endNode) {
            listOf(currentPath)
        } else {
            neighborsOf(currentNode).filter { nextNode ->
                nextNode.size == SIZE.LARGE || nextNode !in currentPath || (nextNode == doubleVisitNode && currentPath.count { it == nextNode } < 2)
            }.flatMap { nextNode ->
                findPathsAllowDoubleVisit(currentPath + nextNode, endNode, doubleVisitNode)
            }
        }
    }

}

fun part1(input: List<String>): Int {
    val (nodes, edges) = parseNodeAndEdges(input)
    val graph = Graph(nodes.values.toList(), edges)

    return graph.allPaths(nodes["start"]!!, nodes["end"]!!).size
}

fun part2(input: List<String>): Int {
    val (nodes, edges) = parseNodeAndEdges(input)
    val graph = Graph(nodes.values.toList(), edges)
    val smallNodesNotStartOrEnd = nodes.values.filter { it.size == SIZE.SMALL && it.name !in listOf("start", "end") }

    val startNode = nodes["start"]!!
    val endNode = nodes["end"]!!
    val singleVisitPaths = graph.allPaths(startNode, endNode)
    val doubleVisitPaths = smallNodesNotStartOrEnd.flatMap { doubleVisitNode ->
        graph.findPathsAllowDoubleVisit(listOf(startNode), endNode, doubleVisitNode)
    }.distinct()

    return (singleVisitPaths + doubleVisitPaths).distinct().size
}

private fun parseNodeAndEdges(input: List<String>): Pair<MutableMap<String, Node>, List<Pair<Node, Node>>> {
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
    return Pair(nodes, edges)
}

fun main() = AoCTask("day12").run {
    // test if implementation meets criteria from the description, like:
    check(part1(testInput) == 10)
    check(part1(readTestInput(2)) == 19)
    check(part1(readTestInput(3)) == 226)
    check(part2(testInput) == 36)
    check(part2(readTestInput(2)) == 103)
    check(part2(readTestInput(3)) == 3509)

    println(part1(input))
    println(part2(input))
}
