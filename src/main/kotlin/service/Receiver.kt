package service

import com.google.gson.JsonObject
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import settings.RecordsChannel

class Receiver: AbstractVerticle() {
    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/").handler(this::handleNewRecord)
        vertx.createHttpServer().requestHandler(router::accept).listen(8080)
    }

    private fun handleNewRecord(routingContext: RoutingContext) {
        val response = routingContext.response()
        val body = routingContext.bodyAsString
        vertx.eventBus().send<JsonObject>(RecordsChannel, body, {
            val msg = if (it.succeeded()) "success" else "error"
            println("result of receiving: $msg")
        })
        response.end()
    }
}
