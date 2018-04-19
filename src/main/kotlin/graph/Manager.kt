package graph

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

const val ManagerControlMax = 5

class Manager: AbstractVerticle() {
    private val graphs: MutableList<Graph> = mutableListOf()

    
    override fun start(startFuture: Future<Void>?) {

    }
}
