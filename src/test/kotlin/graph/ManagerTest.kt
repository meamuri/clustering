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
        assertEquals(0, manager.currentExecutor)
    }

    @Test
    fun `record with different TIDs should be processed in different graphs`() {
        manager.registerRecord(recordGenerator(1, NEO4J_RELATION))
        manager.registerRecord(recordGenerator(2, NEO4J_RELATION))
        assertEquals(0, manager.findTidProcessor(1))
        assertEquals(1, manager.findTidProcessor(2))
        assertEquals(1, manager.currentExecutor)
    }

}