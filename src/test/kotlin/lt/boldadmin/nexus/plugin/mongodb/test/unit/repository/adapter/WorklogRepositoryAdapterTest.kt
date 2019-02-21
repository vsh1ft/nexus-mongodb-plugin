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
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
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

    @Mock
    private lateinit var templateStub: MongoTemplate

    private lateinit var adapter: WorklogRepositoryAdapter

    @Before
    fun setUp() {
        adapter = WorklogRepositoryAdapter(templateStub, userRepositoryAdapterSpy, worklogMongoRepositorySpy)
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
            assertEquals(WORK_LOG_ID, firstValue.id)
        }
    }

    @Test
    fun `Gets workLogs by collaborator id`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy).findByCollaboratorId(COLLABORATOR_ID)

        val actualWorkLogs = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertWorkLogFieldsAreEqual(actualWorkLogs.single())
    }

    @Test
    fun `Gets workLog by interval id and not by work status ordered by ascending timestamp`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy)
            .findByIntervalIdOrderByTimestampAsc(INTERVAL_ID)

        val actualWorkLogs = adapter.findByIntervalIdOrderByLatest(INTERVAL_ID)

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
    fun `Gets latest worklog by project id, collaborator id and not by work status`() {
        val expectedWorklog = createWorkLog()
        val query = Query().apply {
            addCriteria(Criteria.where("project.\$id").`is`(PROJECT_ID))
            addCriteria(Criteria.where("collaborator.\$id").`is`(COLLABORATOR_ID))
            with(Sort(Sort.Direction.DESC, "timestamp"))
            limit(1)
        }
        doReturn(expectedWorklog).`when`(templateStub).findOne(query, Worklog::class.java)

        val actualWorklog = adapter.findLatest(COLLABORATOR_ID, PROJECT_ID)

        assertEquals(expectedWorklog, actualWorklog)
    }

    @Test
    fun `Gets latest workLog by collaborator id`() {
        doReturn(createWorklogClone()).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdOrderByTimestampDesc(COLLABORATOR_ID)

        val actualWorkLog = adapter.findLatest(COLLABORATOR_ID)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Provides null when worklog does not exist`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdOrderByTimestampDesc(COLLABORATOR_ID)

        val actualWorkLog = adapter.findLatest(COLLABORATOR_ID)

        assertNull(actualWorkLog)
    }

    @Test
    fun `Confirms that user doesn't have worklog intervals`() {
        val userId = "userId"
        val worklogClone = createWorklogClone()
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorklogInterval = adapter.doesUserHaveWorklogInterval(userId, INTERVAL_ID)

        assertFalse(hasWorklogInterval)
    }

    @Test
    fun `Confirms that user has worklog intervals`() {
        val userId = "userId"
        val worklogClone = createWorklogClone()
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)
        doReturn(true).`when`(userRepositoryAdapterSpy).doesUserHaveWorklog(eq(userId), any())

        val hasWorklogInterval = adapter.doesUserHaveWorklogInterval(userId, INTERVAL_ID)

        assertTrue(hasWorklogInterval)
    }

    @Test
    fun `Provides status that collaborator has worklog interval`() {
        val worklogClone = createWorklogClone()
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorkLogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertTrue(hasWorkLogInterval)
    }

    @Test
    fun `Provides status that collaborator does not have worklog interval`() {
        val worklogClone = createWorklogClone()
        val worklogWithOtherCollaboratorClone = createWorklogClone(createCollaborator("someOtherCollaboratorId"))
        doReturn(listOf(worklogClone, worklogWithOtherCollaboratorClone))
            .`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorklogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertFalse(hasWorklogInterval)
    }

    @Test
    fun `Provides status that user has worklog interval`() {
        val worklogClone = createWorklogClone()
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorklogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertTrue(hasWorklogInterval)
    }

    @Test
    fun `Confirms that collaborator has work log intervals`() {
        val worklogClone = createWorklogClone()
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorkLogInterval = adapter
            .doesCollaboratorHaveWorklogIntervals(COLLABORATOR_ID, listOf(INTERVAL_ID, INTERVAL_ID))

        assertTrue(hasWorkLogInterval)
    }

    @Test
    fun `Denies that collaborator has worklog intervals`() {
        val worklogClone = createWorklogClone()
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
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    private fun createWorkLog() = Worklog(PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, WORK_LOG_ID)

    private fun createWorklogClone(collaborator: Collaborator = COLLABORATOR) =
        WorklogClone(PROJECT, collaborator, TIMESTAMP, WORK_STATUS, INTERVAL_ID, WORK_LOG_ID)

    private fun createCollaborator(id: String = COLLABORATOR_ID) = Collaborator().apply { this.id = id }

    companion object {
        private val COLLABORATOR_ID = "5a3020603004e0472284a428"
        private val PROJECT_ID = "5a3020603004e0472284a429"

        private val PROJECT = Project(PROJECT_ID)
        private val COLLABORATOR = Collaborator().apply { id = COLLABORATOR_ID }
        private val TIMESTAMP = 123456L
        private val WORK_STATUS = WorkStatus.START
        private val INTERVAL_ID = "1"
        private val WORK_LOG_ID = "WORK_LOG_ID"
        private val SECOND_INTERVAL_ID = "secondIntervalId"

    }

}
