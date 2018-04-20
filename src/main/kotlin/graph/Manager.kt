package graph

import api.Record
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

const val ManagerControlMax = 5

class Manager: AbstractVerticle() {
    private val graphs: MutableList<Graph> = mutableListOf()
    private val tidToProcessor: MutableMap<Int, Int> = mutableMapOf()

    var currentExecutor = -1
        private set

    override fun start(startFuture: Future<Void>?) {

    }

    fun registerRecord(record: Record) {
        val processorIndex = tidToProcessor[record.tid]
        if (processorIndex != null) {
            graphs[processorIndex].registerRecord(record)
            return
        }
        currentExecutor++
        tidToProcessor[record.tid] = currentExecutor
        graphs.add(Graph(record))
    }

    fun findTidProcessor(tid: Int): Int = tidToProcessor[tid] ?: -1

    fun getGraphOfTid(tid: Int): Graph? {
        val index = findTidProcessor(tid)
        return if (index == -1) null else graphs[index]
    }
}
