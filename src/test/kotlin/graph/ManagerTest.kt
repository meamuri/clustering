package graph

import api.Record
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ManagerTest {

    lateinit var recordGenerator: (Int, String) -> Record
    lateinit var manager: Manager

    @Before
    fun setUp() {
        recordGenerator = getTidSpecificGenerator()
        manager = Manager()
    }

    @Test
    fun `first record should create new graph`() {
        manager.registerRecord(recordGenerator(1, NEO4J_RELATION))
        assertEquals(1, manager.countOfGraphs)
    }

    @Test
    fun `record with different TIDs should be processed in different graphs`() {
        manager.registerRecord(recordGenerator(1, NEO4J_RELATION))
        manager.registerRecord(recordGenerator(2, NEO4J_RELATION))
        assertEquals(0, manager.findTidProcessor(1))
        assertEquals(1, manager.findTidProcessor(2))
        assertEquals(2, manager.countOfGraphs)
    }

    @Test
    fun `record with same TIDs should be placed in same processors`() {
        manager.registerRecord(recordGenerator(TID, NEO4J_RELATION))
        manager.registerRecord(recordGenerator(TID, NEO4J_QUERY))
        assertEquals(0, manager.findTidProcessor(TID))
        assertEquals(1, manager.countOfGraphs)
        assertEquals(2, manager.getGraphOfTid(TID)!!.getNodes().size)
    }

    @Test
    fun `huge number of TIDs should be merged`() {
        for (i in 0 until ManagerControlMax) { manager.registerRecord(recordGenerator(i, NEO4J_MATCH)) }
        assertEquals(ManagerControlMax, manager.countOfGraphs)
        manager.registerRecord(recordGenerator(ManagerControlMax, NEO4J_MATCH))
        assertEquals(ManagerControlMax, manager.countOfGraphs)
    }

    @Test
    fun `TIDs after clustering should be mapped correctly`() {
        listOf("a", "b", "c", "d", "e")
                .mapIndexed { index, body -> manager.registerRecord(recordGenerator(index, body)) }
        assertEquals(0, manager.findTidProcessor(0))
        assertEquals(1, manager.findTidProcessor(1))
        assertEquals(2, manager.findTidProcessor(2))
        assertEquals(3, manager.findTidProcessor(3))
        assertEquals(4, manager.findTidProcessor(4))
        val srcToDestPair = manager.getMergePair()
        manager.registerRecord(recordGenerator(ManagerControlMax, "J"))
        assertEquals(0, manager.findTidProcessor(0))
        assertEquals(1, manager.findTidProcessor(1))
        assertEquals(2, manager.findTidProcessor(2))
        assertEquals(3, manager.findTidProcessor(3))
        val tidThatEqualWithProcessorIndexAtThisTest = srcToDestPair.first // a little test hack
        // it's possible because tid has pairing: 0 -> 0, 1 -> 1, 2 -> 2,
        assertEquals(srcToDestPair.second, manager.findTidProcessor(tidThatEqualWithProcessorIndexAtThisTest))
        assertEquals(4, manager.findTidProcessor(ManagerControlMax))
    }

    @Test
    fun `node should be placed in new parent-graph`() {
        val range = (0 until ManagerControlMax)
        range.map{ manager.registerRecord(recordGenerator(it, it.toString())) }
        assertEquals(0, manager.findTidProcessor(0))
        assertEquals(1, manager.findTidProcessor(1))
        assertEquals(2, manager.findTidProcessor(2))
        assertEquals(3, manager.findTidProcessor(3))
        assertEquals(4, manager.findTidProcessor(4))

        manager.registerRecord(recordGenerator(ManagerControlMax, ManagerControlMax.toString()))
        val srcToDestPair = manager.getMergePair()
        val graph = manager.getGraphOfTid(srcToDestPair.second)
        assertEquals(2, graph!!.getNodes().size)
        assertTrue { graph.getNodes().contains(srcToDestPair.second.toString()) }
        assertTrue { graph.getNodes().contains(srcToDestPair.first.toString()) }
    }

    @Test
    fun `current not should be latest after clustering`() {
        val beforeClusteringLastIndex = ManagerControlMax - 1
        val range = (0 .. beforeClusteringLastIndex)
        range.map{ manager.registerRecord(recordGenerator(it, it.toString())) }
        val nodeText = "CURR_NODE_AFTER_ALL"
        manager.registerRecord(recordGenerator(beforeClusteringLastIndex, nodeText))
        manager.registerRecord(recordGenerator(100, "100"))
        val graph = manager.getGraphOfTid(beforeClusteringLastIndex)
        assertEquals("CURR_NODE_AFTER_ALL", graph!!.getCurrentNode().body)
    }

}
