package graph

import api.Record
import java.time.Instant

class Graph(initialRecord: Record) {
    private var currentNode: Int = initialRecord.body.hashCode()
    private val nodes: MutableMap<Int, Node> =
            mutableMapOf(currentNode to Node(initialRecord.body, initialRecord.timestamp))

    fun registerRecord(record: Record) {
        val hashOfNode = record.body.hashCode()
        addNode(hashOfNode, record.body, record.timestamp)
        addRelation(currentNode, hashOfNode, record.timestamp)
        currentNode = hashOfNode
    }

    fun getNodes(): List<String> {
        return nodes.values.map { it.body }
    }

    fun getRelationsOf(nodeBody: String): List<Pair<Int, Weight>>? {
        val address = nodeBody.hashCode()
        if (nodes[address] == null) {
            return null
        }
        return nodes[address]!!.relations.map {it.toPair()}
    }

    private fun addNode(nodeAddress: Int, body: String, timestamp: Instant) {
        nodes[nodeAddress] = Node(body, timestamp)
    }

    private fun addRelation(source: Int, destination: Int, visitedTimestamp: Instant) {
        val relationsOfSource = nodes[source]!!.relations
        if (relationsOfSource[destination] == null) {
            relationsOfSource[destination] = Weight(visitedTimestamp)
        } else {
            relationsOfSource[destination]!!.recompute(visitedTimestamp)
        }
    }

}