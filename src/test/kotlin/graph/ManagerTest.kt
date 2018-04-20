package graph

import api.Record
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ManagerTest {

    lateinit var recordGenerator: (Int, String) -> Record

    @Before
    fun setUp() {
        recordGenerator = getTidSpecificGenerator()
    }

    @Test
    fun `first record should create new graph`() {
        val m = Manager()
        m.registerRecord(recordGenerator(1, NEO4J_RELATION))
        assertEquals(0, m.currentExecutor)
    }
}