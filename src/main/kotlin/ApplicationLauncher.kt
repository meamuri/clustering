import graph.Manager
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import service.NeoVertex
import service.Receiver

private val baseOptions = VertxOptions().setClustered(false)

fun main(args: Array<String>) {
    val vertex = Vertx.vertx(baseOptions)
    vertex.deployVerticle(Manager::class.java.name)
    vertex.deployVerticle(NeoVertex::class.java.name, DeploymentOptions().setWorker(true))
    vertex.deployVerticle(Receiver::class.java.name)

}
