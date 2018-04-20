package graph

import api.Record
import org.junit.Test
import kotlin.test.assertEquals


class GraphTest {

    @Test
    fun `empty graph should create first node`() {
        val graph = Graph(getRecord(SQL_QUERY))
        assertEquals(1, graph.getNodes().size)
    }

    @Test
    fun `graph provide access for creating two and more nodes`() {
        val graph = Graph(getRecord(SQL_QUERY))
        graph.registerRecord(getRecord(NEO4J_QUERY, 1))
        assertEquals(2, graph.getNodes().size)
        assertEquals(1, graph.getRelationsOf(SQL_QUERY)!!.size)
        assertEquals(0, graph.getRelationsOf(NEO4J_QUERY)!!.size)
    }

    @Test
    fun `graph should resolve loop relations`() {
        val graph = Graph(getRecord(NEO4J_RELATION, 1))
        graph.registerRecord(getRecord(NEO4J_RELATION, 1))
        assertEquals(1, graph.getNodes().size)
        assertEquals(1, graph.getRelationsOf(NEO4J_RELATION)!!.size)
    }

    @Test
    fun `graph with strong topology`() {
        val graph = Graph(Record(TID, SQL_QUERY, dayOfLife, listOf("does", "not", "matter")))
        graph.registerRecord(getRecord(NEO4J_RELATION, 1))
        graph.registerRecord(getRecord(NEO4J_MATCH, 2))
        graph.registerRecord(getRecord(SQL_QUERY, 3))
        graph.registerRecord(getRecord(NEO4J_QUERY, 4))
        assertEquals(4, graph.getNodes().size)
        assertEquals(2, graph.getRelationsOf(SQL_QUERY)!!.size)
        assertEquals(1, graph.getRelationsOf(NEO4J_RELATION)!!.size)
        assertEquals(1, graph.getRelationsOf(NEO4J_MATCH)!!.size)
        assertEquals(0, graph.getRelationsOf(NEO4J_QUERY)!!.size)
    }
}
