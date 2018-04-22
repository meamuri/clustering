package service

import com.google.gson.Gson
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.neo4j.driver.v1.*
import service.messaging.ResultType
import service.messaging.WasClustered
import service.messaging.WasCreated
import service.messaging.WasProcessed
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
            "CREATE (t:$validGraphName {body: \$NEW_BODY, ts: \$TS }) \n" +
            "CREATE (f)-[:Linked {min: \$MIN, max: \$MAX, delta: \$DELTA, dispersion: \$D}]->(t)"
    }
    private val addLoopToExistedNode = { validGraphName: String ->
        "MATCH (f:$validGraphName), (t:$validGraphName) where f.body = \$FROM AND t.body = \$TO \n" +
                "CREATE (f)-[:Linked {min: \$MIN, max: \$MAX, delta: \$DELTA, dispersion: \$D}]->(t) \n" +
                "SET t.ts = \$TS"
    }
    private fun appendNodes(from: String, to: String): String =
            "MATCH (g:$from) \n" +
                    "MERGE (v:$to {body: g.body}) \n" +
                    "set v.ts = g.ts"
    private fun mergeEdges(from: String, to: String) =
            "MATCH (prev:$from)-[r:Linked]->(prevr:$from)\n" +
                    "MATCH (g:$to {body: prev.body}), (gr:$to {body: prevr.body})\n" +
                    "MERGE (g)-[w:Linked]->(gr)\n" +
                    "set w = {min:r.min, min: r.max, delta: r.delta, dispersion: r.dispersion}"
    private fun removeOld(graphLabel: String): String =
            "MATCH (prev:$graphLabel)\n" +
                    "detach delete prev"


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
            ResultType.Processed.name -> {
                val json = Gson().fromJson(r.toString(), WasProcessed::class.java)
                if (json.isLoop) {
                    writeLoopRelation(json.label, json.prevBody, json.body,
                            Instant.ofEpochMilli(json.ts), json.min, json.max, json.delta, json.dispersion)
                } else {
                    writeNodeAndLinking(json.label, json.prevBody, json.body,
                            Instant.ofEpochMilli(json.ts), json.min, json.max, json.delta, json.dispersion)
                }
            }
            ResultType.Clustered.name -> {
                val json = Gson().fromJson(r.toString(), WasClustered::class.java)
                clusterIt(json.labelBefore, json.labelAfter)
            }
            else -> return
        }
    }

    private fun writeLoopRelation(graphLabel: String, prevNode: String,
                                  body: String, ts: Instant, min: Long,
                                  max: Long, delta: Long, dispersion: Double) {
        val session = driver.session()

        session.use {
            session.writeTransaction { tx ->
                tx.run(addLoopToExistedNode(graphLabel),
                        Values.parameters(
                                "FROM", prevNode,
                                "TO", body,
                                "TS", ts.toEpochMilli(),
                                "MIN", min,
                                "MAX", max,
                                "DELTA", delta,
                                "D", dispersion
                        ))
            }
        } // .. session use
    }

    private fun writeNodeAndLinking(graphLabel: String, prevNode: String,
                                    body: String, ts: Instant, min: Long,
                                    max: Long, delta: Long, dispersion: Double) {
        val session = driver.session()

        session.use {
            session.writeTransaction { tx ->
                tx.run(addNodeAndLinking(graphLabel),
                        Values.parameters(
                                "BODY", prevNode,
                                "NEW_BODY", body,
                                "TS", ts.toEpochMilli(),
                                "MIN", min,
                                "MAX", max,
                                "DELTA", delta,
                                "D", dispersion
                        ))
            }
        } // .. session use
    }

    private fun writeNode(graphLabel: String, body: String, ts: Instant) {
        val session = driver.session()

        session.use {
             session.writeTransaction { tx ->
                tx.run(addNodeQuery(graphLabel),
                        Values.parameters(
                                "BODY", body,
                                "TS", ts.toEpochMilli().toString()
                        ))
            }
        } // .. session use
    } // fun writeNode

    private fun clusterIt(labelBefore: String, labelAfter: String) {
        val session = driver.session()

        session.use {
            session.writeTransaction { tx ->
                tx.run(appendNodes(labelBefore, labelAfter))
                tx.run(mergeEdges(labelBefore, labelAfter))
                tx.run(removeOld(labelBefore))
            }
        } // .. session use
    }

}