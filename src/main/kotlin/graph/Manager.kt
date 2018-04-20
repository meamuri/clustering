package graph

import api.Record
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

const val ManagerControlMax = 5

class Manager: AbstractVerticle() {
    private val graphs: MutableList<Graph> = mutableListOf()
    var currentExecutor = -1
        private set

    override fun start(startFuture: Future<Void>?) {

    }

    fun registerRecord(record: Record) {
        currentExecutor++
        graphs.add(Graph(record))
    }
}
