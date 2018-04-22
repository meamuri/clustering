package graph

import api.Record
import java.time.Instant


class Graph(initialRecord: Record) {
    private var currentNode: Int = initialRecord.body.hashCode()
    val nodes: MutableMap<Int, Node> =
            mutableMapOf(currentNode to Node(initialRecord.body, Instant.ofEpochMilli(initialRecord.timestamp)))

    fun registerRecord(record: Record) {
        val timestamp = Instant.ofEpochMilli(record.timestamp)
        val hashOfNode = record.body.hashCode()
        if (hashOfNode == currentNode) {
            addLoop(timestamp)
            return
        }
        addNode(hashOfNode, record.body, timestamp)
        addRelation(currentNode, hashOfNode, timestamp)
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

    fun merge(graph: Graph) {
        graph.nodes.forEach({
            if (it.key !in nodes.keys) {
                nodes[it.key.hashCode()] = it.value
            } else {
                val existedNodeRelations = nodes[it.key]!!.relations
                it.value.relations.forEach({
                    if (it.key in existedNodeRelations.keys) {
                        existedNodeRelations[it.key]!!.recompute(it.value)
                    } else {
                        existedNodeRelations[it.key] = it.value
                    }
                })
            }
        })
        if (getCurrentNode().timestamp < graph.getCurrentNode().timestamp)
            currentNode = graph.currentNode
    }

    private fun addNode(nodeAddress: Int, body: String, timestamp: Instant) {
        if (nodes[nodeAddress] == null) {
            nodes[nodeAddress] = Node(body, timestamp)
        } else {
            nodes[nodeAddress]!!.timestamp = timestamp
        }
    }

    private fun addRelation(source: Int, destination: Int, visitedTimestamp: Instant) {
        val relationsOfSource = nodes[source]!!.relations
        relationsOfSource[destination]?.recompute(visitedTimestamp)
                ?: relationsOfSource.put(destination, Weight(visitedTimestamp))
    }

    private fun addLoop(timestamp: Instant) {
        nodes[currentNode]!!.timestamp = timestamp
        val selfRelation = nodes[currentNode]!!.relations
        selfRelation[currentNode]?.recompute(timestamp) ?: selfRelation.put(currentNode, Weight(timestamp))
    }

    fun getCurrentNode(): Node = nodes[currentNode]!!

}
