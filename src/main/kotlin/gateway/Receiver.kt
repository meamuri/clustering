package gateway

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class Receiver: AbstractVerticle() {
    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/").handler(this::handleNewRecord)
        vertx.createHttpServer().requestHandler(router::accept).listen(8080)
    }

    private fun handleNewRecord(routingContext: RoutingContext) {
        val response = routingContext.response()
        val body = routingContext.bodyAsJson
        vertx.eventBus().publish("records-feed", body)
        response.end()
    }

}