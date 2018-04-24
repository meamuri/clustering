import graph.Manager
import helpers.initDatabase
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import service.NeoVertex
import service.Receiver
import settings.WithBaseInitialization

private val baseOptions = VertxOptions().setClustered(false)

fun main(args: Array<String>) {
    val vertex = Vertx.vertx(baseOptions)
    vertex.deployVerticle(Manager::class.java.name)
    vertex.deployVerticle(NeoVertex::class.java.name, DeploymentOptions().setWorker(true))
    vertex.deployVerticle(Receiver::class.java.name)
    if (WithBaseInitialization()) {
        initDatabase(vertex.eventBus())
    }
}
