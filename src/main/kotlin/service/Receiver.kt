package service

import api.Record
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import settings.Port
import settings.RecordsChannel

class Receiver: AbstractVerticle() {
    private val path = "/"

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post(path).handler(this::handleNewRecord)
        vertx.createHttpServer().requestHandler(router::accept).listen(Port)
    }

    private fun handleNewRecord(routingContext: RoutingContext) {
        println("get request for processing")
        val response = routingContext.response()

        val record = routingContext.bodyAsString
        if (!isCorrectRecord(record)) {
            println("incorrect request data")
            response.setStatusCode(400).end()
            return
        }

        println("request was processed")
        vertx.eventBus().send<String>(RecordsChannel, record, {
            val msg = if (it.succeeded()) "success" else "error"
            println("result of receiving: $msg")
        })
        response.end()
    }

    private fun isCorrectRecord(record: String): Boolean {
        return Record.fromJsonString(record) != null
    }

}
