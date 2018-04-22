package service

import api.Record
import graph.Graph
import graph.Manager
import helpers.edgesToJsonObject
import helpers.toJsonObject
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import settings.GraphSaverChannel
import settings.RecordsChannel

class Core: AbstractVerticle() {
    private val manager = Manager()
    override fun start() {
        vertx.eventBus().consumer<String>(RecordsChannel, this::recordHandler)

        vertx.setPeriodic(15000, this::snapShotPeriodic)
    }

    private fun recordHandler(message: Message<String>) {
        val record = Record.fromJsonString(message.body())
        if (record != null) {
            manager.registerRecord(record)
        }
        message.reply("ok")
    }

    private fun snapShotPeriodic (l: Long) {
        manager.graphs.forEach({
            val jsonGraph = it.toJsonObject()
            vertx.eventBus().send(GraphSaverChannel, jsonGraph)
        })
    }

}
