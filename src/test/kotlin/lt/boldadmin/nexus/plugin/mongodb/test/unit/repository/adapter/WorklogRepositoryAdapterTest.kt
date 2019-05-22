package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.aggregation.WorklogInterval
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
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
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
    fun `Gets worklogs by collaborator id`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy).findByCollaboratorId(COLLABORATOR_ID)

        val actualWorklogs = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertWorklogFieldsAreEqual(actualWorklogs.single())
    }

    @Test
    fun `Gets worklog by interval id and not by work status ordered by ascending timestamp`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy)
            .findByIntervalIdOrderByTimestampAsc(INTERVAL_ID)

        val actualWorklogs = adapter.findByIntervalIdOrderByLatest(INTERVAL_ID)

        assertWorklogFieldsAreEqual(actualWorklogs.single())
    }

    @Test
    fun `Gets worklogs by project id`() {
        doReturn(listOf(createWorklogClone())).`when`(worklogMongoRepositorySpy)
            .findByProjectId(PROJECT_ID)

        val actualWorklogs = adapter.findByProjectId(PROJECT_ID)

        assertWorklogFieldsAreEqual(actualWorklogs.single())
    }

    @Test
    fun `Gets latest worklog by project id, collaborator id and not by work status`() {
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
    fun `Gets aggregated interval ids by collaborator id`() {
        val expectedIntervalIds = getStubedAggregationResults().mappedResults.map { it.intervalId }.toList()

        val actualIntervalIds = adapter.findIntervalIdsByCollaboratorId("id")

        assertEquals(expectedIntervalIds, actualIntervalIds)
    }

    @Test
    fun `Gets aggregated interval ids by project id`() {
        val expectedIntervalIds = getStubedAggregationResults().mappedResults.map { it.intervalId }.toList()

        val actualIntervalIds = adapter.findIntervalIdsByProjectId("id")

        assertEquals(expectedIntervalIds, actualIntervalIds)
    }

    @Test
    fun `Gets interval ids by collaborator id and started work status`() {
        val expectedAggregation = createAggregation("collaborator.\$id", COLLABORATOR_ID)
        getStubedAggregationResults()

        adapter.findIntervalIdsByCollaboratorId(COLLABORATOR_ID)

        assertEquals(expectedAggregation, getCapturedAggregation())
    }

    @Test
    fun `Gets interval ids by project id and started work status`() {
        val expectedAggregation = createAggregation("project.\$id", PROJECT_ID)
        getStubedAggregationResults()

        adapter.findIntervalIdsByProjectId(PROJECT_ID)

        assertEquals(expectedAggregation, getCapturedAggregation())
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

    private fun createWorklogClone(collaborator: Collaborator = COLLABORATOR) =
        WorklogClone(PROJECT, collaborator, TIMESTAMP, WORK_STATUS, INTERVAL_ID, WORKLOG_ID)

    private fun createCollaborator(id: String = COLLABORATOR_ID) = Collaborator().apply { this.id = id }

    private fun createWorklogInterval() = WorklogInterval().apply { this.intervalId = INTERVAL_ID }

    private fun getStubedAggregationResults(): AggregationResults<WorklogInterval> {
        val aggregationResults = AggregationResults(listOf(createWorklogInterval()), Document())
        doReturn(aggregationResults).`when`(templateStub)
            .aggregate(any(), eq(Worklog::class.java), eq(WorklogInterval::class.java))

        return aggregationResults
    }

    private fun createAggregation(key: String, id: String) = newAggregation(
        match(
            where(key).`is`(id)
                .andOperator(where("workStatus").`is`("START"))
        )
    ).toString()

    private fun getCapturedAggregation(): String {
        var capturedValue = ""
        argumentCaptor<Aggregation>().apply {
            verify(templateStub).aggregate(capture(), eq(Worklog::class.java), eq(WorklogInterval::class.java))
            capturedValue = firstValue.toString()
        }
        return capturedValue
    }

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

