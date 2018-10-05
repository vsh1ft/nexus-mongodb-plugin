package lt.boldadmin.nexus.plugin.mongodb.test.unit.adapter

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.adapter.WorkLogRepositoryMongodbGatewayAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.WorkLogClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class WorkLogRepositoryMongodbGatewayAdapterTest {

    @Mock
    private lateinit var workLogRepositorySpy: WorkLogRepository

    private lateinit var adapter: WorkLogRepositoryMongodbGatewayAdapter

    @Before
    fun setUp() {
        adapter = WorkLogRepositoryMongodbGatewayAdapter(workLogRepositorySpy)
    }

    @Test
    fun `Saves workLog as a clone`() {
        val workLog = createWorkLog()

        adapter.save(workLog)

        argumentCaptor<WorkLogClone>().apply {
            verify(workLogRepositorySpy).save(capture())
            assertEquals(PROJECT, firstValue.project)
            assertEquals(COLLABORATOR, firstValue.collaborator)
            assertEquals(TIMESTAMP, firstValue.timestamp)
            assertEquals(WORK_STATUS, firstValue.workStatus)
            assertEquals(INTERVAL_ID, firstValue.intervalId)
            assertEquals(DESCRIPTION, firstValue.description)
            assertEquals(WORK_LOG_ID, firstValue.id)
        }
    }

    @Test
    fun `Retrieves real workLog object by id`() {
        doReturn(true).`when`(workLogRepositorySpy).existsByIntervalId(INTERVAL_ID)

        val exists = adapter.existsByIntervalId(INTERVAL_ID)

       assertTrue(exists)
    }

    @Test
    fun `Retrieves real workLogs by collaborator id`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogRepositorySpy).findByCollaboratorId(COLLABORATOR_ID)

        val actualWorkLogs = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertEquals(PROJECT, actualWorkLogs.first().project)
        assertEquals(COLLABORATOR, actualWorkLogs.first().collaborator)
        assertEquals(TIMESTAMP, actualWorkLogs.first().timestamp)
        assertEquals(WORK_STATUS, actualWorkLogs.first().workStatus)
        assertEquals(INTERVAL_ID, actualWorkLogs.first().intervalId)
        assertEquals(DESCRIPTION, actualWorkLogs.first().description)
        assertEquals(WORK_LOG_ID, actualWorkLogs.first().id)
    }

    @Test
    fun `Retrieves real workLogs by interval id`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogRepositorySpy).findByIntervalId(INTERVAL_ID)

        val actualWorkLogs = adapter.findByIntervalId(INTERVAL_ID)

        assertEquals(PROJECT, actualWorkLogs.first().project)
        assertEquals(COLLABORATOR, actualWorkLogs.first().collaborator)
        assertEquals(TIMESTAMP, actualWorkLogs.first().timestamp)
        assertEquals(WORK_STATUS, actualWorkLogs.first().workStatus)
        assertEquals(INTERVAL_ID, actualWorkLogs.first().intervalId)
        assertEquals(DESCRIPTION, actualWorkLogs.first().description)
        assertEquals(WORK_LOG_ID, actualWorkLogs.first().id)
    }

    @Test
    fun `Retrieves real workLogs by interval id and workStatus`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogRepositorySpy)
            .findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLogs = adapter.findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(INTERVAL_ID, WORK_STATUS)

        assertEquals(PROJECT, actualWorkLogs.first().project)
        assertEquals(COLLABORATOR, actualWorkLogs.first().collaborator)
        assertEquals(TIMESTAMP, actualWorkLogs.first().timestamp)
        assertEquals(WORK_STATUS, actualWorkLogs.first().workStatus)
        assertEquals(INTERVAL_ID, actualWorkLogs.first().intervalId)
        assertEquals(DESCRIPTION, actualWorkLogs.first().description)
        assertEquals(WORK_LOG_ID, actualWorkLogs.first().id)
    }

    @Test
    fun `Retrieves real workLogs by project id`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogRepositorySpy)
            .findByProjectId(PROJECT_ID)

        val actualWorkLogs = adapter.findByProjectId(PROJECT_ID)

        assertEquals(PROJECT, actualWorkLogs.first().project)
        assertEquals(COLLABORATOR, actualWorkLogs.first().collaborator)
        assertEquals(TIMESTAMP, actualWorkLogs.first().timestamp)
        assertEquals(WORK_STATUS, actualWorkLogs.first().workStatus)
        assertEquals(INTERVAL_ID, actualWorkLogs.first().intervalId)
        assertEquals(DESCRIPTION, actualWorkLogs.first().description)
        assertEquals(WORK_LOG_ID, actualWorkLogs.first().id)
    }

    @Test
    fun `Retrieves real latest interval endpoint object`() {
        doReturn(createWorkLogClone()).`when`(workLogRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        assertEquals(PROJECT, actualWorkLog!!.project)
        assertEquals(COLLABORATOR, actualWorkLog.collaborator)
        assertEquals(TIMESTAMP, actualWorkLog.timestamp)
        assertEquals(WORK_STATUS, actualWorkLog.workStatus)
        assertEquals(INTERVAL_ID, actualWorkLog.intervalId)
        assertEquals(DESCRIPTION, actualWorkLog.description)
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    @Test
    fun `Retrieves null if there is not latest interval endpoint`() {
        doReturn(null).`when`(workLogRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        assertNull(actualWorkLog)
    }

    @Test
    fun `Retrieves latest real workLog object by interval id`() {
        doReturn(createWorkLogClone()).`when`(workLogRepositorySpy).findFirstByIntervalId(INTERVAL_ID)

        val actualWorkLog = adapter.findFirstByIntervalId(INTERVAL_ID)

        assertEquals(PROJECT, actualWorkLog.project)
        assertEquals(COLLABORATOR, actualWorkLog.collaborator)
        assertEquals(TIMESTAMP, actualWorkLog.timestamp)
        assertEquals(WORK_STATUS, actualWorkLog.workStatus)
        assertEquals(INTERVAL_ID, actualWorkLog.intervalId)
        assertEquals(DESCRIPTION, actualWorkLog.description)
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    @Test
    fun `Retrieves latest real workLog object by interval id and workStatus ordered`() {
        doReturn(createWorkLogClone()).`when`(workLogRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        assertEquals(PROJECT, actualWorkLog!!.project)
        assertEquals(COLLABORATOR, actualWorkLog.collaborator)
        assertEquals(TIMESTAMP, actualWorkLog.timestamp)
        assertEquals(WORK_STATUS, actualWorkLog.workStatus)
        assertEquals(INTERVAL_ID, actualWorkLog.intervalId)
        assertEquals(DESCRIPTION, actualWorkLog.description)
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    @Test
    fun `Retrieves null if there is no workLog by interval id and workStatus ordered`() {
        doReturn(null).`when`(workLogRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        assertNull(actualWorkLog)
    }

    private fun createWorkLogClone() =
        WorkLogClone(
            PROJECT,
            COLLABORATOR,
            TIMESTAMP,
            WORK_STATUS,
            INTERVAL_ID,
            DESCRIPTION,
            WORK_LOG_ID
        )

    fun createWorkLog() =
        WorkLog(
            PROJECT,
            COLLABORATOR,
            TIMESTAMP,
            WORK_STATUS,
            INTERVAL_ID,
            DESCRIPTION,
            WORK_LOG_ID
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