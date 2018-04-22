package service

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.GraphDatabase
import org.neo4j.driver.v1.Values
import settings.GraphSaverChannel
import java.time.Instant


class NeoVertex: AbstractVerticle() {
    private val connAddress = "bolt://localhost:7687"
    private val username = "diplom-admin"
    private val password = "admin"

    private val addNodeQuery = "CREATE (a:\$tid {body: \$body, ts: \$ts})"
    private val driver = GraphDatabase.driver(connAddress, AuthTokens.basic(username, password))

    override fun start() {
        vertx.eventBus().consumer<JsonObject>(GraphSaverChannel, this::graphSnapshotHandler)
    }

    override fun stop() {
        driver.closeAsync()
    }

    private fun graphSnapshotHandler(message: Message<JsonObject>) {
        message.body()
    }

    private fun writeNode(tid: Int, body: String, ts: Instant) {
        val session = driver.session()

        session.use {
             session.writeTransaction { tx ->
                val result = tx.run(addNodeQuery,
                        Values.parameters(
                                "tid", tid,
                                "body", body,
                                "ts", ts
                        ))
                result.single().get( 0 ).asString()
            }
        } // .. session use
    } // fun writeNode

}