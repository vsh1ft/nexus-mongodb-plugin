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
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
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

        val actualWorkLog = adapter.findLatestWithWorkStatusNot(COLLABORATOR_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets latest worklog by project id, collaborator id and not by work status`() {
        val expectedWorklog = createWorkLog()
        val query = Query().apply {
            addCriteria(Criteria.where("project.\$id").`is`(ObjectId(PROJECT_ID)))
            addCriteria(Criteria.where("collaborator.\$id").`is`(ObjectId(COLLABORATOR_ID)))
            addCriteria(Criteria.where("workStatus").ne(WORK_STATUS))
            with(Sort(Sort.Direction.DESC, "timestamp"))
            limit(1)
        }
        doReturn(expectedWorklog).`when`(templateStub).findOne(query, Worklog::class.java)

        val actualWorklog = adapter.findLatestWithWorkStatusNot(COLLABORATOR_ID, PROJECT_ID, WORK_STATUS)

        assertEquals(expectedWorklog, actualWorklog)
    }

    @Test
    fun `Gets null if there is not latest interval endpoint by collaborator id and status`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(COLLABORATOR_ID, WORK_STATUS)

        val actualWorkLog = adapter.findLatestWithWorkStatusNot(COLLABORATOR_ID, WORK_STATUS)

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

        val actualWorkLog = adapter.findLatest(INTERVAL_ID, WORK_STATUS)

        assertWorkLogFieldsAreEqual(actualWorkLog!!)
    }

    @Test
    fun `Gets null if there is no workLog by interval id and workStatus`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(INTERVAL_ID, WORK_STATUS)

        val actualWorklog = adapter.findLatest(INTERVAL_ID, WORK_STATUS)

        assertNull(actualWorklog)
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
        assertEquals(DESCRIPTION, actualWorkLog.description)
        assertEquals(WORK_LOG_ID, actualWorkLog.id)
    }

    private fun createWorkLog() = Worklog(
        PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
    )

    private fun createWorklogClone(collaborator: Collaborator = COLLABORATOR) = WorklogClone(
        PROJECT, collaborator, TIMESTAMP, WORK_STATUS, INTERVAL_ID, DESCRIPTION, WORK_LOG_ID
    )

    private fun createCollaborator(id: String = COLLABORATOR_ID) = Collaborator().apply { this.id = id }

    companion object {
        private val COLLABORATOR_ID = "5a3020603004e0472284a428"
        private val PROJECT_ID = "5a3020603004e0472284a429"

        private val PROJECT = Project(PROJECT_ID)
        private val COLLABORATOR = Collaborator().apply { id = COLLABORATOR_ID }
        private val TIMESTAMP = 123456L
        private val WORK_STATUS = WorkStatus.START
        private val INTERVAL_ID = "1"
        private val DESCRIPTION = "DESCRIPTION"
        private val WORK_LOG_ID = "WORK_LOG_ID"
        private val SECOND_INTERVAL_ID = "secondIntervalId"

    }

}
