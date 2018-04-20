package graph

import org.junit.Test
import kotlin.test.assertEquals

class ManagerTest {

    @Test
    fun `first record should create new graph`() {
        val m = Manager()
        m.registerRecord()
        assertEquals(0, m.currentExecutor)
    }
}