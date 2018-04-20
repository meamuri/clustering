package graph

import api.Record
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class GraphTest {
    lateinit var recordGenerator: (String) -> Record

    @Before
    fun setUp() {
        recordGenerator = getRecordGenerator()
    }

    @Test
    fun `empty graph should create first node`() {
        val graph = Graph(recordGenerator(SQL_QUERY))
        assertEquals(1, graph.getNodes().size)
    }

    @Test
    fun `graph provide access for creating two and more nodes`() {
        val graph = Graph(recordGenerator(SQL_QUERY))
        graph.registerRecord(recordGenerator(NEO4J_QUERY))
        assertEquals(2, graph.getNodes().size)
        assertEquals(1, graph.getRelationsOf(SQL_QUERY)!!.size)
        assertEquals(0, graph.getRelationsOf(NEO4J_QUERY)!!.size)
    }

    @Test
    fun `graph should resolve loop relations`() {
        val graph = Graph(recordGenerator(NEO4J_RELATION))
        graph.registerRecord(recordGenerator(NEO4J_RELATION))
        assertEquals(1, graph.getNodes().size)
        assertEquals(1, graph.getRelationsOf(NEO4J_RELATION)!!.size)
    }

    @Test
    fun `graph with strong topology`() {
        val graph = Graph(Record(TID, SQL_QUERY, dayOfLife, listOf("does", "not", "matter")))
        graph.registerRecord(recordGenerator(NEO4J_RELATION))
        graph.registerRecord(recordGenerator(NEO4J_MATCH))
        graph.registerRecord(recordGenerator(SQL_QUERY))
        graph.registerRecord(recordGenerator(NEO4J_QUERY))
        assertEquals(4, graph.getNodes().size)
        assertEquals(2, graph.getRelationsOf(SQL_QUERY)!!.size)
        assertEquals(1, graph.getRelationsOf(NEO4J_RELATION)!!.size)
        assertEquals(1, graph.getRelationsOf(NEO4J_MATCH)!!.size)
        assertEquals(0, graph.getRelationsOf(NEO4J_QUERY)!!.size)
    }
}
