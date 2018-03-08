package edu.vsu.graph

import edu.vsu.api.Record


class Graph (tid: String, record: Record) {
    val id = tid
    private val root = record.body.hashCode() // hash of root vertex
    private val vertices = mutableMapOf(root to Vertex(record.body)) // hash -> Vertex
    private var currVertex = root // hash of last vertex

    fun addVertex(record: Record) {
        val hash = record.body.hashCode()
        vertices[currVertex]!!.addChild(record, hash)
        if (hash !in vertices.keys) {
            vertices[hash] = Vertex(record.body)
        }
        currVertex = hash
    }
}