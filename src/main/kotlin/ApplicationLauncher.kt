import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions

private val baseOptions = VertxOptions().setClustered(false)

fun main(args: Array<String>) {
    deployVertex("db.NeoVertex", true)
    deployVertex("gateway.Receiver")
    deployVertex("graph.Manager")
}

fun deployVertex(vertexName: String, isWorker: Boolean = false) {
    val depOpts = DeploymentOptions()
    depOpts.isWorker = isWorker
    Vertx.vertx(baseOptions).deployVerticle(vertexName, depOpts)
}
