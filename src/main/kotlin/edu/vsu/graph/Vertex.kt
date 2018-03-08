package edu.vsu.graph

import edu.vsu.api.Record

class Vertex(body: String) {
    data class Weight(var timestamp: Long, private var n: Int = 0) {
        fun recompute(record: Record){
            timestamp = record.timestamp
            n++
        }
    }

    val info = body
    private val edges = mutableMapOf<Int, Weight>()

    fun addChild(record: Record, hash: Int) {
        if (hash in edges.keys) {
            edges[hash]!!.recompute(record)
        } else {
            edges[hash] = Weight(record.timestamp)
        }
    }
}