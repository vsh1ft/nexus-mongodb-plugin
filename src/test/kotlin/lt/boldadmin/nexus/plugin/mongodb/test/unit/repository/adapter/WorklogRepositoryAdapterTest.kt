package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.mongodb.client.DistinctIterable
import com.mongodb.client.MongoCollection
import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.DateRange
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.UserRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.WorklogRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.bson.Document
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate
import kotlin.test.*

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
    fun `Saves worklog as a clone`() {
        val worklog = createWorklog()
        doAnswer { invocation ->
            val worklogClone = invocation.arguments[0] as WorklogClone
            worklogClone.apply { id = WORKLOG_ID }
        }.`when`(worklogMongoRepositorySpy).save<WorklogClone>(any())

        adapter.save(worklog)

        argumentCaptor<WorklogClone>().apply {
            verify(worklogMongoRepositorySpy).save(capture())
            assertEquals(PROJECT, firstValue.project)
            assertEquals(COLLABORATOR, firstValue.collaborator)
            assertEquals(TIMESTAMP, firstValue.timestamp)
            assertEquals(WORK_STATUS, firstValue.workStatus)
            assertEquals(INTERVAL_ID, firstValue.intervalId)
            assertEquals(WORKLOG_ID, firstValue.id)
        }
    }

    @Test
    fun `Gets worklog by interval id and not by work status ordered by ascending timestamp`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy)
            .findByIntervalIdOrderByTimestampAsc(INTERVAL_ID)

        val actualWorklogs = adapter.findByIntervalIdOrderByLatest(INTERVAL_ID)

        assertWorklogFieldsAreEqual(actualWorklogs.single())
    }

    @Test
    fun `Gets latest worklog by project id, collaborator id`() {
        val expectedWorklog = createWorklog()
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
    fun `Gets latest worklog by collaborator id`() {
        doReturn(createWorklogClone()).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdOrderByTimestampDesc(COLLABORATOR_ID)

        val actualWorklog = adapter.findLatest(COLLABORATOR_ID)

        assertWorklogFieldsAreEqual(actualWorklog!!)
    }

    @Test
    fun `Gets interval ids by collaborator id`() {
        val query = Query().addCriteria(where("collaborator.\$id").`is`(COLLABORATOR_ID))
        stubQueryForWorklogIntervalIds(query)

        val actualIntervalIds = adapter.findIntervalIdsByCollaboratorId(COLLABORATOR_ID)

        assertEquals(listOf(INTERVAL_ID), actualIntervalIds)
    }

    @Test
    fun `Gets interval ids by project id`() {
        val query = Query().addCriteria(where("project.\$id").`is`(PROJECT_ID))
        stubQueryForWorklogIntervalIds(query)

        val actualIntervalIds = adapter.findIntervalIdsByProjectId(PROJECT_ID)

        assertEquals(listOf(INTERVAL_ID), actualIntervalIds)
    }

    @Test
    fun `Gets interval ids by project id and date range`() {
        val query = Query().addCriteria(where("project.\$id").`is`(PROJECT_ID))
            .addCriteria(where("timestamp").gte(1558742400000).lte(1558915199999))
            .addCriteria(where("workStatus").`is`("START"))
        stubQueryForWorklogIntervalIds(query)
        val dateRange = DateRange(LocalDate.of(2019, 5, 25), LocalDate.of(2019, 5, 26))

        val actualIntervalIds = adapter.findIntervalIdsByProjectId(PROJECT_ID, dateRange)

        assertEquals(listOf(INTERVAL_ID), actualIntervalIds)
    }

    @Test
    fun `Gets interval ids by collaborator id and date range`() {
        val query = Query().addCriteria(where("collaborator.\$id").`is`(COLLABORATOR_ID))
            .addCriteria(where("timestamp").gte(1558742400000).lte(1558915199999))
            .addCriteria(where("workStatus").`is`("START"))
        stubQueryForWorklogIntervalIds(query)
        val dateRange = DateRange(LocalDate.of(2019, 5, 25), LocalDate.of(2019, 5, 26))

        val actualIntervalIds = adapter.findIntervalIdsByCollaboratorId(COLLABORATOR_ID, dateRange)

        assertEquals(listOf(INTERVAL_ID), actualIntervalIds)
    }

    @Test
    fun `Provides null when worklog does not exist`() {
        doReturn(null).`when`(worklogMongoRepositorySpy)
            .findFirstByCollaboratorIdOrderByTimestampDesc(COLLABORATOR_ID)

        val actualWorklog = adapter.findLatest(COLLABORATOR_ID)

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

        val hasWorklogInterval = adapter.doesCollaboratorHaveWorklogInterval(COLLABORATOR_ID, INTERVAL_ID)

        assertTrue(hasWorklogInterval)
    }

    @Test
    fun `Provides status that collaborator does not have worklog interval`() {
        val worklogClone = createWorklogClone()
        val worklogWithOtherCollaboratorClone = createWorklogClone(createOtherCollaborator())
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
    fun `Confirms that collaborator has worklog intervals`() {
        val worklogClone = createWorklogClone()
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)

        val hasWorkLogInterval = adapter
            .doesCollaboratorHaveWorklogIntervals(COLLABORATOR_ID, listOf(INTERVAL_ID, INTERVAL_ID))

        assertTrue(hasWorkLogInterval)
    }

    @Test
    fun `Denies that collaborator has worklog intervals`() {
        val worklogClone = createWorklogClone()
        val worklogWithOtherCollaboratorClone = createWorklogClone(createOtherCollaborator())
        doReturn(listOf(worklogClone, worklogClone)).`when`(worklogMongoRepositorySpy).findByIntervalId(INTERVAL_ID)
        doReturn(listOf(worklogClone, worklogWithOtherCollaboratorClone))
            .`when`(worklogMongoRepositorySpy)
            .findByIntervalId(SECOND_INTERVAL_ID)

        val hasWorklogInterval = adapter
            .doesCollaboratorHaveWorklogIntervals(
                COLLABORATOR_ID, listOf(
                    INTERVAL_ID,
                    SECOND_INTERVAL_ID
                )
            )

        assertFalse(hasWorklogInterval)
    }

    private fun assertWorklogFieldsAreEqual(actualWorklog: Worklog) {
        assertEquals(PROJECT, actualWorklog.project)
        assertEquals(COLLABORATOR, actualWorklog.collaborator)
        assertEquals(TIMESTAMP, actualWorklog.timestamp)
        assertEquals(WORK_STATUS, actualWorklog.workStatus)
        assertEquals(INTERVAL_ID, actualWorklog.intervalId)
        assertEquals(WORKLOG_ID, actualWorklog.id)
    }

    private fun createWorklog() = Worklog(PROJECT, COLLABORATOR, TIMESTAMP, WORK_STATUS, INTERVAL_ID, WORKLOG_ID)

    private fun stubQueryForWorklogIntervalIds(query: Query) {
        val collectionStub: MongoCollection<Document> = mock()
        val iterableStub: DistinctIterable<Collection<String>> = mock()
        doReturn(collectionStub).`when`(templateStub).getCollection("worklog")
        doReturn(iterableStub)
            .`when`(collectionStub)
            .distinct(same("intervalId"), eq(query.queryObject), any<Class<String>>())
        doReturn(listOf(INTERVAL_ID)).`when`(iterableStub).into(mutableListOf())
    }

    private fun createWorklogClone(collaborator: Collaborator = COLLABORATOR) =
        WorklogClone(PROJECT, collaborator, TIMESTAMP, WORK_STATUS, INTERVAL_ID, WORKLOG_ID)

    private fun createOtherCollaborator() = Collaborator().apply { this.id = "someOtherCollaboratorId" }

    companion object {
        private val COLLABORATOR_ID = "5a3020603004e0472284a428"
        private val PROJECT_ID = "5a3020603004e0472284a429"

        private val PROJECT = Project(PROJECT_ID)
        private val COLLABORATOR = Collaborator().apply { id = COLLABORATOR_ID }
        private val TIMESTAMP = 123456L
        private val WORK_STATUS = WorkStatus.START
        private val INTERVAL_ID = "1"
        private val WORKLOG_ID = "WORKLOG_ID"
        private val SECOND_INTERVAL_ID = "secondIntervalId"

    }

}
