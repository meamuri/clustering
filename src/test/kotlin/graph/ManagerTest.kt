package graph

import api.Record
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

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

}