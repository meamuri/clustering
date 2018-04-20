package service

import api.Record
import graph.Manager
import io.vertx.core.AbstractVerticle
import settings.RecordsChannel

class Core: AbstractVerticle() {
    private val manager = Manager()
    override fun start() {
        vertx.eventBus().consumer<String>(RecordsChannel, {
            val record = Record.fromJsonString(it.body())
            if (record != null) {
                manager.registerRecord(record)
            }
            it.reply("ok")
        })

        vertx.setPeriodic(15000, {
            println(manager.countOfGraphs)
        })
    }

}
