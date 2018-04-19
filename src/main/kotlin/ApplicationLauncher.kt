import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject

private val baseOptions = VertxOptions().setClustered(false)
private val workerOptions = DeploymentOptions(JsonObject(mapOf("worker" to true)))

fun main(args: Array<String>) {
    Vertx.vertx(baseOptions).deployVerticle("db.NeoVertex")
    Vertx.vertx(baseOptions).deployVerticle("gateway.Receiver", workerOptions)
}