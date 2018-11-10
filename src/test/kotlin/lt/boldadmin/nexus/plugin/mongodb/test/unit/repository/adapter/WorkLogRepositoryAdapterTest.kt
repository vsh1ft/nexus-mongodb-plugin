package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.WorkLogRepositoryAdapter
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
class WorkLogRepositoryAdapterTest {

    @Mock
    private lateinit var worklogMongoRepositorySpy: WorklogMongoRepository

    private lateinit var adapter: WorkLogRepositoryAdapter

    @Before
    fun setUp() {
        adapter = WorkLogRepositoryAdapter(worklogMongoRepositorySpy)
    }

    @Test
    fun `Saves workLog as a clone`() {
        val workLog = createWorkLog()
        doAnswer { invocation ->
            val workLogClone = invocation.arguments[0] as WorkLogClone
            workLogClone.apply { id = WORK_LOG_ID }
        }.`when`(worklogMongoRepositorySpy).save<WorkLogClone>(any())

        adapter.save(workLog)

        argumentCaptor<WorkLogClone>().apply {
            verify(worklogMongoRepositorySpy).save(capture())
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
        doReturn(Optional.of(createWorkLogClone())).`when`(worklogMongoRepositorySpy).findById(WORK_LOG_ID)

        val actualWorkLog = adapter.findById(WORK_LOG_ID)

        assertWorkLogFieldsAreEqual(actualWorkLog)
    }


    @Test
    fun `Confirms that workLog exist by id`() {
        doReturn(true).`when`(worklogMongoRepositorySpy).existsByIntervalId(INTERVAL_ID)

        val exists = adapter.existsByIntervalId(INTERVAL_ID)

        assertTrue(exists)
    }

    @Test
    fun `Gets workLogs by collaborator id`() {
        doReturn(listOf(createWorkLogClone())).`when`(worklogMongoRepositorySpy).findByCollaboratorId(COLLABORATOR_ID)

        val actualWorkLogs = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLogs by interval id`() {
        doReturn(listOf(createWorkLogClone())).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val actualWorkLogs = adapter.findByIntervalId(INTERVAL_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLog by interval id and not by work status ordered by ascending timestamp`() {
        doReturn(listOf(createWorkLogClone())).`when`(worklogMongoRepositorySpy)
            .findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLogs = adapter.findByIntervalIdAndWorkStatusNotOrderByLatest(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLogs by project id`() {
        doReturn(listOf(createWorkLogClone())).`when`(worklogMongoRepositorySpy)
            .findByProjectId(PROJECT_ID)

        val actualWorkLogs = adapter.findByProjectId(PROJECT_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets latest worklog by collaborator id and not by work status`() {
        doReturn(createWorkLogClone()).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestByCollaboratorIdAndWorkStatusNot(COLLABORATOR_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets null if there is not latest interval endpoint by collaborator id and status`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestByCollaboratorIdAndWorkStatusNot(COLLABORATOR_ID, WORK_STATUS)

        assertNull(actualWorkLog)
    }

    @Test
    fun `Gets latest workLog by interval id`() {
        doReturn(createWorkLogClone()).`when`(worklogMongoRepositorySpy).findFirstByIntervalId(INTERVAL_ID)

        val actualWorkLog = adapter.findFirstByIntervalId(INTERVAL_ID)

        assertWorkLogFieldsAreEqual(actualWorkLog)
    }

    @Test
    fun `Gets latest workLog by interval id and workStatus`() {
        doReturn(createWorkLogClone()).`when`(worklogMongoRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestByIntervalIdAndWorkStatus(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets null if there is no workLog by interval id and workStatus`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestByIntervalIdAndWorkStatus(INTERVAL_ID, WORK_STATUS)

        assertNull(actualWorkLog)
    }

    @Test
    fun `Gets distinct worklogs by collaborator and by projectId`() {
        val expectedWorklog = createWorkLogClone()
        val anotherExpectedWorkLog = createWorkLogClone(createCollaborator("new"))

        doReturn(listOf(expectedWorklog, anotherExpectedWorkLog, createWorkLogClone()))
            .`when`(worklogMongoRepositorySpy).findByProjectId(PROJECT_ID)

        val actualWorklogs = adapter.findByDistinctCollaboratorIdAndProjectId(PROJECT_ID)

        assertEquals(expectedWorklog.get().id, actualWorklogs[0].id)
        assertEquals(anotherExpectedWorkLog.get().id, actualWorklogs[1].id)
        assertEquals(2, actualWorklogs.size)
    }

    @Test
    fun `Confirms that collaborator is not working`() {
        doReturn(createWorkLogClone(workStatus = WorkStatus.END))
            .`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
                COLLABORATOR_ID,
                WorkStatus.DESCRIPTION_UPDATE
            )
        val isNotWorking = adapter.hasWorkEnded(COLLABORATOR_ID)

        assertTrue(isNotWorking)
    }

    @Test
    fun `Gets working collaborators by project id`() {
        val startedWorklog = createWorkLogClone()
        val endedWorklog = createWorkLogClone(createCollaborator("new"), WorkStatus.END)
        doReturn(listOf(startedWorklog, endedWorklog)).`when`(worklogMongoRepositorySpy).findByProjectId(PROJECT_ID)
        doReturn(startedWorklog).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
                COLLABORATOR_ID,
                WorkStatus.DESCRIPTION_UPDATE
            )
        doReturn(endedWorklog).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc("new", WorkStatus.DESCRIPTION_UPDATE)

        val actualCollaborators = adapter.findWorkingCollaboratorsByProjectId(PROJECT_ID)

        assertEquals(startedWorklog.collaborator.id, actualCollaborators.first().id)
        assertEquals(1, actualCollaborators.size)
    }

    private fun assertWorkLogFieldsAreEqual(actualWorkLog: Worklog) {
        assertEquals(PROJECT, actualWorkLog.project)
        assertEquals(COLLABORATOR, actualWorkLog.collaborator)
        assertEquals(TIMESTAMP, actualWorkLog.timestamp)
        assertEquals(WORK_STATUS, actualWorkLog.workStatus)
        assertEquals(INTERVAL_ID, actualWorkLog.intervalId)
        assertEquals(DESCRIPTION, actualWorkLog.description)
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    private fun createWorkLog() = Worklog(
        PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, null
    )

    companion object {
        private val COLLABORATOR_ID = "COLLABORATOR_ID"
        private val PROJECT_ID = "PROJECT_ID"

        private val PROJECT = createProject()
        private val COLLABORATOR = createCollaborator()
        private val TIMESTAMP = 123456L
        private val WORK_STATUS = WorkStatus.START
        private val INTERVAL_ID = "1"
        private val DESCRIPTION = "DESCRIPTION"
        private val WORK_LOG_ID = "WORK_LOG_ID"

        private fun createWorkLogClone(
            collaborator: Collaborator = COLLABORATOR,
            workStatus: WorkStatus = WORK_STATUS,
            project: Project = PROJECT
        ) = WorkLogClone(
            project, collaborator, TIMESTAMP, workStatus, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
        )

        private fun createProject(projectId: String = PROJECT_ID) = Project().apply { this.id = projectId }

        private fun createCollaborator(id: String = COLLABORATOR_ID) =
            Collaborator().apply { this.id = id }

    }

}