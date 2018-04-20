package graph

import api.Record
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

const val ManagerControlMax = 5

class Manager: AbstractVerticle() {
    private val graphs: MutableList<Graph> = mutableListOf()
    private val tidToProcessor: MutableMap<Int, Int> = mutableMapOf()

    val countOfGraphs get() = graphs.size

    override fun start(startFuture: Future<Void>?) {

    }

    fun registerRecord(record: Record) {
        val processorIndex = tidToProcessor[record.tid]
        if (processorIndex != null) {
            graphs[processorIndex].registerRecord(record)
            return
        }
        if (countOfGraphs == ManagerControlMax) {
            clustering()
        }
        graphs.add(Graph(record))
        tidToProcessor[record.tid] = graphs.lastIndex
    }

    private fun clustering() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun findTidProcessor(tid: Int): Int = tidToProcessor[tid] ?: -1

    fun getGraphOfTid(tid: Int): Graph? {
        val index = findTidProcessor(tid)
        return if (index == -1) null else graphs[index]
    }
}
