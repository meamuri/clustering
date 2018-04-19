package graph

import java.time.Instant

data class Node(val body: String, var timestamp: Instant) {
    val relations: MutableMap<Int, Weight> = mutableMapOf()
}