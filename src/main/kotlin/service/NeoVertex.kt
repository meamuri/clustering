package service

import com.google.gson.Gson
import graph.Weight
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.neo4j.driver.v1.*
import service.messaging.ResultType
import service.messaging.WasCreated
import settings.GraphSaverChannel
import java.time.Instant


class NeoVertex: AbstractVerticle() {
    private val connAddress = "bolt://localhost:7687"
    private val username = "diplom-admin"
    private val password = "admin"

    private val addNodeQuery = { validGraphName:String ->
        "CREATE (a:$validGraphName) \n" +
                "SET a.body = \$BODY, a.ts = \$TS"
    }
    private val addNodeAndLinking = { validGraphName: String -> "MATCH (f:$validGraphName) where f.body = \$BODY \n" +
            "CREATE (t:$validGraphName {body: \$NEW_BODY, {ts: \$NEW_TS }) \n" +
            "CREATE (f)-[:Linked {min: \$MIN, max: \$MAX}, delta: \$DELTA, dispersion: \$D]->(t)"
    }


    private val driver = GraphDatabase.driver(connAddress, AuthTokens.basic(username, password))

    override fun start() {
        vertx.eventBus().consumer<JsonObject>(GraphSaverChannel, this::graphSnapshotHandler)
    }

    override fun stop() {
        driver.closeAsync()
    }

    private fun graphSnapshotHandler(message: Message<JsonObject>) {
        val r = message.body()
        when (r.getString("resultType")) {
            ResultType.Created.name -> {
                val wasCreated = Gson().fromJson(r.toString(), WasCreated::class.java)
                writeNode(wasCreated.label, wasCreated.body, Instant.ofEpochMilli(wasCreated.ts))
            }
            else -> return
        }
    }


    private fun writeNodeAndLinking(graphLabel: String, prevNode: String,
                                    body: String, ts: Instant, w: Weight) {
        val session = driver.session()

        session.use {
            session.writeTransaction { tx ->
                val result = tx.run(addNodeAndLinking(graphLabel),
                        Values.parameters(
                                "BODY", prevNode,
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
//                val result = tx.run(addNodeQuery(graphLabel),
                tx.run(addNodeQuery(graphLabel),
                        Values.parameters(
                                "BODY", body,
                                "TS", ts.toEpochMilli().toString()
                        ))
//                result.single().get( 0 ).asString()
            }
        } // .. session use
    } // fun writeNode

}