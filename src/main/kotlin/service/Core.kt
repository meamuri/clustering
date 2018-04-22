package service

import api.Record
import graph.Manager
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import settings.RecordsChannel

class Core: AbstractVerticle() {
    private val manager = Manager()
    override fun start() {
        vertx.eventBus().consumer<String>(RecordsChannel, this::recordHandler)

        vertx.setPeriodic(15000, {
            println(manager.countOfGraphs)
        })
    }

    private fun recordHandler(message: Message<String>) {
        val record = Record.fromJsonString(message.body())
        if (record != null) {
            manager.registerRecord(record)
        }
        message.reply("ok")
    }

}
