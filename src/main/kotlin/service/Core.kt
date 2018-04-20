package service

import api.Record
import com.google.gson.Gson
import com.google.gson.JsonObject
import graph.Manager
import io.vertx.core.AbstractVerticle

class Core: AbstractVerticle() {
    private val manager = Manager()
    override fun start() {
        vertx.eventBus().consumer<JsonObject>("records-feed", {
            val record = Gson().fromJson(it.body(), Record::class.java)
            manager.registerRecord(record)
            println("$record was added to cluster")
        })

        vertx.setPeriodic(15000, {
            println(manager.countOfGraphs)
        })
    }
}
