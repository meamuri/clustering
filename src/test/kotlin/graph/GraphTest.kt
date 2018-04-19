package graph

import api.Record
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

// https://currentmillis.com/
const val dayOfLife: Long = 1523739600000                   // 15 april 2018
const val dayInMilliseconds: Long = 86400000                // 24 hours


const val TID = 1
const val SQL_QUERY = "SELECT ? FROM ?"
const val NEO4J_QUERY = "CREATE (?:? {? , ?}) )"
const val NEO4J_MATCH = "MATCH (n:?)-[:?]->(friends) return n"
const val NEO4J_RELATION = "CREATE (?)-[:?]->(ir),(?)-[:?]->(?)"

class GraphTest {

    @Test
    fun `empty graph should create first node`() {
        val graph = Graph(Record(TID, SQL_QUERY, Instant.ofEpochSecond(dayOfLife), listOf("*", "tableName")))
        assertEquals(1, graph.getNodes().size)
    }

    @Test
    fun `graph provide access for creating two and more nodes`() {
        val graph = Graph(Record(TID, SQL_QUERY, Instant.ofEpochSecond(dayOfLife), listOf("*", "tableName")))
        graph.registerRecord(
            Record(TID, NEO4J_QUERY, Instant.ofEpochSecond(dayOfLife + dayInMilliseconds),
                    listOf("node", "label", "key", "value"))
        )
        assertEquals(2, graph.getNodes().size)
        assertEquals(1, graph.getRelationsOf(SQL_QUERY)!!.size)
        assertEquals(0, graph.getRelationsOf(NEO4J_QUERY)!!.size)
    }

    @Test
    fun `graph should resolve loop relations`() {
        val graph = Graph(Record(TID, NEO4J_RELATION, Instant.ofEpochSecond(dayOfLife), listOf("does", "not", "matter")))
        graph.registerRecord(
                Record(TID, NEO4J_RELATION, Instant.ofEpochSecond(dayOfLife + dayInMilliseconds),
                        listOf("node", "label", "key", "value"))
        )
        assertEquals(1, graph.getNodes().size)
        assertEquals(1, graph.getRelationsOf(NEO4J_RELATION)!!.size)
    }

    @Test
    fun `graph with strong topology`() {
        val graph = Graph(Record(TID, SQL_QUERY, Instant.ofEpochSecond(dayOfLife), listOf("does", "not", "matter")))
        graph.registerRecord(
                Record(TID, NEO4J_RELATION, Instant.ofEpochSecond(dayOfLife + dayInMilliseconds),
                        listOf("node", "label", "key", "value"))
        )
        graph.registerRecord(
                Record(TID, NEO4J_MATCH, Instant.ofEpochSecond(dayOfLife + dayInMilliseconds),
                        listOf("node", "label", "key", "value"))
        )
        graph.registerRecord(
                Record(TID, SQL_QUERY, Instant.ofEpochSecond(dayOfLife + dayInMilliseconds),
                        listOf("node", "label", "key", "value"))
        )
        graph.registerRecord(
                Record(TID, NEO4J_QUERY, Instant.ofEpochSecond(dayOfLife + dayInMilliseconds),
                        listOf("node", "label", "key", "value"))
        )
        assertEquals(4, graph.getNodes().size)
        assertEquals(2, graph.getRelationsOf(SQL_QUERY)!!.size)
        assertEquals(1, graph.getRelationsOf(NEO4J_RELATION)!!.size)
        assertEquals(1, graph.getRelationsOf(NEO4J_MATCH)!!.size)
        assertEquals(0, graph.getRelationsOf(NEO4J_QUERY)!!.size)
    }
}
