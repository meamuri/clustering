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
}