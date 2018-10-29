package lt.boldadmin.nexus.plugin.mongodb.test.unit.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorkLogClone
import org.junit.Test
import kotlin.test.assertEquals

class WorkLogTest {

    @Test
    fun `Sets worklog clone`() {
        val expectedClone = createWorkLogClone()

        val actualClone = WorkLogClone().apply { set(createWorkLog()) }

        assertEquals(expectedClone.project, actualClone.project)
        assertEquals(expectedClone.collaborator, actualClone.collaborator)
        assertEquals(expectedClone.timestamp, actualClone.timestamp)
        assertEquals(expectedClone.workStatus, actualClone.workStatus)
        assertEquals(expectedClone.intervalId, actualClone.intervalId)
        assertEquals(expectedClone.description, actualClone.description)
        assertEquals(expectedClone.id, actualClone.id)
    }

    @Test
    fun `Converts clone to User`() {
        val expectedUser = createWorkLog()

        val actualUser = createWorkLogClone().get()

        assertEquals(expectedUser.project, actualUser.project)
        assertEquals(expectedUser.collaborator, actualUser.collaborator)
        assertEquals(expectedUser.timestamp, actualUser.timestamp)
        assertEquals(expectedUser.workStatus, actualUser.workStatus)
        assertEquals(expectedUser.intervalId, actualUser.intervalId)
        assertEquals(expectedUser.description, actualUser.description)
        assertEquals(expectedUser.id, actualUser.id)
    }

    private fun createWorkLogClone() = WorkLogClone(
        PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
    )

    private fun createWorkLog() = Worklog(
        PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
    )

    companion object {
        private val COLLABORATOR_ID = "COLLABORATOR_ID"
        private val PROJECT_ID = "PROJECT_ID"

        private val PROJECT = Project().apply { id = PROJECT_ID }
        private val COLLABORATOR = Collaborator().apply { id = COLLABORATOR_ID }
        private val TIMESTAMP = 123456L
        private val WORK_STATUS = WorkStatus.START
        private val INTERVAL_ID = "1"
        private val DESCRIPTION = "DESCRIPTION"
        private val WORK_LOG_ID = "WORK_LOG_ID"
    }

}

