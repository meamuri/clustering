package db

import io.vertx.core.AbstractVerticle
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.GraphDatabase
import org.neo4j.driver.v1.Values
import java.time.Instant

private const val connAddress = "bolt://localhost:7687"
private const val username = "diplom-admin"
private const val password = "admin"

class NeoVertex: AbstractVerticle() {
    private val addNodeQuery = "CREATE (a:\$tid {body: \$body, ts: \$ts})"
    private val driver = GraphDatabase.driver(connAddress, AuthTokens.basic(username, password))

    override fun start() {

    }

    override fun stop() {
        driver.closeAsync()
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