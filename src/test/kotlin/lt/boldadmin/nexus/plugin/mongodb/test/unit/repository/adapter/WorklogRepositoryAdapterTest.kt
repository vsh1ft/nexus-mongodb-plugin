package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.UserRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.WorklogRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class WorklogRepositoryAdapterTest {

    @Mock
    private lateinit var worklogMongoRepositorySpy: WorklogMongoRepository

    @Mock
    private lateinit var userRepositoryAdapterSpy: UserRepositoryAdapter

    private lateinit var adapter: WorklogRepositoryAdapter

    @Before
    fun setUp() {
        adapter = WorklogRepositoryAdapter(worklogMongoRepositorySpy, userRepositoryAdapterSpy)
    }

    @Test
    fun `Saves workLog as a clone`() {
        val workLog = createWorkLog()
        doAnswer { invocation ->
            val workLogClone = invocation.arguments[0] as WorklogClone
            workLogClone.apply { id = WORK_LOG_ID }
        }.`when`(worklogMongoRepositorySpy).save<WorklogClone>(any())

        adapter.save(workLog)

        argumentCaptor<WorklogClone>().apply {
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
        doReturn(Optional.of(createWorklogClone())).`when`(worklogMongoRepositorySpy).findById(WORK_LOG_ID)

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
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy).findByCollaboratorId(COLLABORATOR_ID)

        val actualWorkLogs = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLogs by interval id`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val actualWorkLogs = adapter.findByIntervalId(INTERVAL_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLog by interval id and not by work status ordered by ascending timestamp`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy)
            .findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLogs = adapter.findByIntervalIdAndWorkStatusNotOrderByLatest(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLogs by project id`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy)
            .findByProjectId(PROJECT_ID)

        val actualWorkLogs = adapter.findByProjectId(PROJECT_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets latest worklog by collaborator id and not by work status`() {
        doReturn(createWorklogClone()).`when`(worklogMongoRepositorySpy)
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
        doReturn(createWorklogClone()).`when`(worklogMongoRepositorySpy).findFirstByIntervalId(INTERVAL_ID)

        val actualWorkLog = adapter.findFirstByIntervalId(INTERVAL_ID)

        assertWorkLogFieldsAreEqual(actualWorkLog)
    }

    @Test
    fun `Gets latest workLog by interval id and workStatus`() {
        doReturn(createWorklogClone()).`when`(worklogMongoRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestByIntervalIdAndWorkStatus(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets null if there is no workLog by interval id and workStatus`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorklog = adapter.findLatestByIntervalIdAndWorkStatus(INTERVAL_ID, WORK_STATUS)

        assertNull(actualWorklog)
    }

    @Test
    fun `Confirms that worklog exists by project id and collaborator id`() {
        doReturn(true).`when`(worklogMongoRepositorySpy).existsByProjectIdAndCollaboratorId(PROJECT_ID, COLLABORATOR_ID)

        val exists = adapter.existsByProjectIdAndCollaboratorId(PROJECT_ID, COLLABORATOR_ID)

        assertTrue(exists)
    }

    @Test
    fun `Confirms that user doesn't have worklog intervals`() {
        val userId = "userId"
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorklogInterval = adapter.doesUserHaveWorklogInterval(userId, INTERVAL_ID)

        assertFalse(hasWorklogInterval)
    }

    @Test
    fun `Confirms that user has worklog intervals`() {
        val userId = "userId"
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)
        doReturn(true).`when`(userRepositoryAdapterSpy).doesUserHaveWorklog(eq(userId), any())

        val hasWorklogInterval = adapter.doesUserHaveWorklogInterval(userId, INTERVAL_ID)

        assertTrue(hasWorklogInterval)
    }

    @Test
    fun `Provides status that collaborator has worklog interval`() {
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorkLogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertTrue(hasWorkLogInterval)
    }

    @Test
    fun `Provides status that collaborator does not have worklog interval`() {
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        val worklogWithOtherCollaboratorClone = createWorklogClone(createCollaborator("someOtherCollaboratorId"))
        doReturn(listOf(worklogClone, worklogWithOtherCollaboratorClone))
            .`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorklogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertFalse(hasWorklogInterval)
    }

    @Test
    fun `Provides status that user has worklog interval`() {
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorklogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertTrue(hasWorklogInterval)
    }

    @Test
    fun `Confirms that collaborator has work log intervals`() {
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorkLogInterval = adapter
            .doesCollaboratorHaveWorklogIntervals(COLLABORATOR_ID, listOf(INTERVAL_ID, INTERVAL_ID))

        assertTrue(hasWorkLogInterval)
    }

    @Test
    fun `Denies that collaborator has worklog intervals`() {
        val worklogClone = createWorklogClone(createCollaborator(COLLABORATOR_ID))
        val worklogWithOtherCollaboratorClone = createWorklogClone(createCollaborator("someOtherCollaboratorId"))
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)
        doReturn(listOf(worklogClone, worklogWithOtherCollaboratorClone))
            .`when`(worklogMongoRepositorySpy)
            .findByIntervalId(SECOND_INTERVAL_ID)

        val hasWorkLogInterval = adapter
            .doesCollaboratorHaveWorklogIntervals(
                COLLABORATOR_ID, listOf(
                    INTERVAL_ID,
                    SECOND_INTERVAL_ID
                )
            )

        assertFalse(hasWorkLogInterval)
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
        PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
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
        private val SECOND_INTERVAL_ID = "secondIntervalId"

        private fun createWorklogClone(
            collaborator: Collaborator = COLLABORATOR,
            workStatus: WorkStatus = WORK_STATUS,
            project: Project = PROJECT
        ) = WorklogClone(
            project, collaborator, TIMESTAMP, workStatus, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
        )

        private fun createProject(projectId: String = PROJECT_ID) = Project().apply { this.id = projectId }

        private fun createCollaborator(id: String = COLLABORATOR_ID) =
            Collaborator().apply { this.id = id }

    }

}