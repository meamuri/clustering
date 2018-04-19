package graph

import api.Record

class Graph {
    private val nodes: MutableMap<Int, Node> = HashMap()

    fun registerRecord(record: Record) {
        val hashOfNode = record.body.hashCode()
        nodes[hashOfNode] = Node(record.body)
    }

    fun getNodes(): List<String> {
        return nodes.values.map { it.body }
    }

}