package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorklogRepository
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.DateRange
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class WorklogRepositoryAdapter(
    private val template: MongoTemplate,
    private val userRepositoryAdapter: UserRepositoryAdapter,
    private val worklogMongoRepository: WorklogMongoRepository
): WorklogRepository {

    override fun findIntervalIdsByCollaboratorId(collaboratorId: String, dateRange: DateRange): Collection<String> {
        val query = createQuery(mapOf("collaborator.\$id" to collaboratorId, "workStatus" to "START"))
            .addCriteria(where("timestamp").gte(dateRange.startToEpochMilli()).lte(dateRange.endToEpochMilli()))

        return findDistinctWorklogIntervalIds(query)
    }

    override fun findIntervalIdsByProjectId(projectId: String, dateRange: DateRange): Collection<String> {
        val query = createQuery(mapOf("project.\$id" to projectId, "workStatus" to "START"))
            .addCriteria(where("timestamp").gte(dateRange.startToEpochMilli()).lte(dateRange.endToEpochMilli()))

        return findDistinctWorklogIntervalIds(query)
    }

    override fun save(worklog: Worklog) {
        val worklogClone = WorklogClone().apply { set(worklog) }
        worklogMongoRepository.save(worklogClone)
    }

    override fun findIntervalIdsByCollaboratorId(collaboratorId: String) =
        findDistinctWorklogIntervalIds(createQuery(mapOf("collaborator.\$id" to collaboratorId)))

    override fun findIntervalIdsByProjectId(projectId: String) =
        findDistinctWorklogIntervalIds(createQuery(mapOf("project.\$id" to projectId)))

    override fun findByIntervalIdOrderByLatest(intervalId: String) =
        worklogMongoRepository.findByIntervalIdOrderByTimestampAsc(intervalId).map { it.get() }

    override fun findLatest(collaboratorId: String, projectId: String): Worklog? {
        val query = createQuery(mapOf("project.\$id" to projectId, "collaborator.\$id" to collaboratorId))
            .with(Sort(Sort.Direction.DESC, "timestamp"))
            .limit(1)

        return template.findOne(query, Worklog::class.java)
    }

    override fun findLatest(collaboratorId: String) =
        worklogMongoRepository.findFirstByCollaboratorIdOrderByTimestampDesc(collaboratorId)?.get()

    override fun doesUserHaveWorklogInterval(userId: String, intervalId: String) =
        findByIntervalId(intervalId).all { userRepositoryAdapter.doesUserHaveWorklog(userId, it) }

    override fun doesCollaboratorHaveWorklogIntervals(collaboratorId: String, intervalIds: Collection<String>) =
        intervalIds.all { doesCollaboratorHaveWorklogInterval(collaboratorId, it) }

    override fun doesCollaboratorHaveWorklogInterval(collaboratorId: String, intervalId: String) =
        findByIntervalId(intervalId).all { it.collaborator.id == collaboratorId }

    private fun findByIntervalId(intervalId: String): Collection<Worklog> =
        worklogMongoRepository.findByIntervalId(intervalId).map { it.get() }

    private fun findDistinctWorklogIntervalIds(query: Query): Collection<String> {
        return template
            .getCollection("worklog")
            .distinct("intervalId", query.queryObject, String::class.java)
            .into(mutableListOf())
    }

    private fun createQuery(keyValues: Map<String, String>): Query {
        val query = Query()
        keyValues.forEach { query.addCriteria(where(it.key).`is`(it.value)) }
        return query
    }

    private fun LocalDate.toEpochMilli(time: LocalTime) = this.atTime(time).toInstant(ZoneOffset.UTC).toEpochMilli()

    private fun DateRange.startToEpochMilli() = start.toEpochMilli(LocalTime.MIN)

    private fun DateRange.endToEpochMilli() = end.toEpochMilli(LocalTime.MAX)
}
