package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.*
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.WorkLogRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogMongodbRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorkLogClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class WorkLogMongodbRepositoryAdapterTest {

    @Mock
    private lateinit var workLogMongodbRepositorySpy: WorkLogMongodbRepository

    private lateinit var adapter: WorkLogRepositoryAdapter

    @Before
    fun setUp() {
        adapter = WorkLogRepositoryAdapter(workLogMongodbRepositorySpy)
    }

    @Test
    fun `Saves workLog as a clone`() {
        val workLog = createWorkLog()
        doAnswer { invocation ->
            val workLogClone = invocation.arguments[0] as WorkLogClone
            workLogClone.apply { id = WORK_LOG_ID }
        }.`when`(workLogMongodbRepositorySpy).save<WorkLogClone>(any())

        adapter.save(workLog)

        argumentCaptor<WorkLogClone>().apply {
            verify(workLogMongodbRepositorySpy).save(capture())
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
    fun `Gets workLog by id`() {
        doReturn(Optional.of(createWorkLogClone())).`when`(workLogMongodbRepositorySpy).findById(WORK_LOG_ID)

        val actualWorkLog = adapter.findById(WORK_LOG_ID)

        assertWorkLogFieldsAreEqual(actualWorkLog)
    }


    @Test
    fun `Confirms that workLog exist by id`() {
        doReturn(true).`when`(workLogMongodbRepositorySpy).existsByIntervalId(INTERVAL_ID)

        val exists = adapter.existsByIntervalId(INTERVAL_ID)

       assertTrue(exists)
    }

    @Test
    fun `Gets workLogs by collaborator id`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogMongodbRepositorySpy).findByCollaboratorId(COLLABORATOR_ID)

        val actualWorkLogs = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLogs by interval id`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogMongodbRepositorySpy).findByIntervalId(INTERVAL_ID)

        val actualWorkLogs = adapter.findByIntervalId(INTERVAL_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLog interval endpoints`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogMongodbRepositorySpy)
            .findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLogs = adapter.findIntervalEndpointsAsc(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLogs by project id`() {
        doReturn(listOf(createWorkLogClone())).`when`(workLogMongodbRepositorySpy)
            .findByProjectId(PROJECT_ID)

        val actualWorkLogs = adapter.findByProjectId(PROJECT_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets latest interval endpoint by collaborator id and status`() {
        doReturn(createWorkLogClone()).`when`(workLogMongodbRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter
            .findLatestIntervalEnpointByCollaboratorId(COLLABORATOR_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets null if there is not latest interval endpoint by collaborator id and status`() {
        doReturn(null).`when`(workLogMongodbRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter
            .findLatestIntervalEnpointByCollaboratorId(COLLABORATOR_ID, WORK_STATUS)

        assertNull(actualWorkLog)
    }

    @Test
    fun `Gets latest workLog by interval id`() {
        doReturn(createWorkLogClone()).`when`(workLogMongodbRepositorySpy).findFirstByIntervalId(INTERVAL_ID)

        val actualWorkLog = adapter.findFirstByIntervalId(INTERVAL_ID)

        assertWorkLogFieldsAreEqual(actualWorkLog)
    }

    @Test
    fun `Gets latest workLog by interval id and workStatus`() {
        doReturn(createWorkLogClone()).`when`(workLogMongodbRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestIntervalEnpointByIntervalId(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets null if there is no workLog by interval id and workStatus`() {
        doReturn(null).`when`(workLogMongodbRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestIntervalEnpointByIntervalId(INTERVAL_ID, WORK_STATUS)

        assertNull(actualWorkLog)
    }

    private fun assertWorkLogFieldsAreEqual(actualWorkLog: WorkLog) {
        assertEquals(PROJECT, actualWorkLog.project)
        assertEquals(COLLABORATOR, actualWorkLog.collaborator)
        assertEquals(TIMESTAMP, actualWorkLog.timestamp)
        assertEquals(WORK_STATUS, actualWorkLog.workStatus)
        assertEquals(INTERVAL_ID, actualWorkLog.intervalId)
        assertEquals(DESCRIPTION, actualWorkLog.description)
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    private fun createWorkLogClone() = WorkLogClone(
        PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
    )

    private fun createWorkLog() =
        WorkLog(
            PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, null
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