package service

import api.Record
import com.google.gson.Gson
import graph.Manager
import io.vertx.core.AbstractVerticle
import settings.RecordsChannel

class Core: AbstractVerticle() {
    private val manager = Manager()
    override fun start() {
        vertx.eventBus().consumer<String>(RecordsChannel, {
            val record = Gson().fromJson(it.body(), Record::class.java)
            manager.registerRecord(record)
        })

        vertx.setPeriodic(15000, {
            println(manager.countOfGraphs)
        })
    }
}
