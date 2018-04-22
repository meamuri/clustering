package service

import graph.Weight
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.neo4j.driver.v1.*
import settings.GraphSaverChannel
import java.time.Instant
import java.time.LocalTime


class NeoVertex: AbstractVerticle() {
    private val connAddress = "bolt://localhost:7687"
    private val username = "diplom-admin"
    private val password = "admin"

    private val addNodeQuery = "CREATE (a:\$GRAPH {body: \$BODY, ts: \$TS})"
    private val addNodeAndLinking = "MATCH (f:\$GRAPH) where f.body = \$BODY" +
            "CREATE (t:\$GRAPH_SAME {body: \$NEW_BODY, {ts: \$NEW_TS })" +
            "CREATE (f)-[:Linked {min: \$MIN, max: \$MAX}, delta: \$DELTA, dispersion: \$D]->(t)"


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


    private fun writeNodeAndLinking(graphLabel: String, prevNode: String,
                                    body: String, ts: Instant, w: Weight) {
        val session = driver.session()

        session.use {
            session.writeTransaction { tx ->
                val result = tx.run(addNodeQuery,
                        Values.parameters(
                                "GRAPH", graphLabel,
                                "BODY", prevNode,
                                "GRAPH_SAME", graphLabel,
                                "NEW_BODY", body,
                                "TS", ts.toEpochMilli(),
                                "MIN", w.min,
                                "MAX", w.max,
                                "DELTA", w.delta,
                                "D", w.dispersion
                        ))
                result.single().get( 0 ).asString()
            }
        } // .. session use
    }

    private fun writeNode(graphLabel: String, body: String, ts: Instant) {
        val session = driver.session()

        session.use {
             session.writeTransaction { tx ->
                val result = tx.run(addNodeAndLinking,
                        Values.parameters(
                                "GRAPH", graphLabel,
                                "BODY", body,
                                "TS", ts.toEpochMilli()
                        ))
                result.single().get( 0 ).asString()
            }
        } // .. session use
    } // fun writeNode

}