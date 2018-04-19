package graph

import api.Record
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

const val timeStampOf_2018_03_30: Long = 1522357200000
const val TID = 1
const val SIMPLE_QUERY = "SELECT ? FROM ?"

class GraphTest {

    @Test
    fun `empty graph should create first node`() {
        val graph: Graph = Graph()
        val r = Record(TID, SIMPLE_QUERY, Instant.ofEpochSecond(timeStampOf_2018_03_30), listOf("*", "tableName"))
        graph.registerRecord(r)
        assertEquals(1, graph.getNodes().size())
    }
}