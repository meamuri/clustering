package graph

import api.Record
import org.junit.Before
import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertTrue

class MetricTest {
    private val eps = 0.00000001
    lateinit var recordGenerator: (String) -> Record

    @Before
    fun setUp() {
        recordGenerator = getRecordGenerator()
    }

    @Test
    fun `equal graphs metric should be equal to one`() {
        val graph = Graph(recordGenerator(NEO4J_MATCH))
        val otherGraph = Graph(recordGenerator(NEO4J_MATCH))
        val metric = getMetrics(graph, otherGraph)

        assertTrue { abs(metric - 1) < eps }
    }

    @Test
    fun `graphs without equals nodes have zero metric value`() {
        val graph = Graph(recordGenerator(NEO4J_MATCH))
        val otherGraph = Graph(recordGenerator(NEO4J_QUERY))

        val metric = getMetrics(graph, otherGraph)
        assertTrue { abs(metric) < eps }
    }

    @Test
    fun `half similar nodes should be equal to 50% metric value`() {
        val graph = Graph(recordGenerator(NEO4J_MATCH))
        val otherGraph = Graph(recordGenerator(NEO4J_QUERY))

        graph.registerRecord(recordGenerator(NEO4J_RELATION))
        otherGraph.registerRecord(recordGenerator(NEO4J_RELATION))

        val metric = getMetrics(graph, otherGraph)
        assertTrue { abs(metric) - 0.5 < eps }
    }

}