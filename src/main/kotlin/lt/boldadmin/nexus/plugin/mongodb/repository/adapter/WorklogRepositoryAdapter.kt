package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorklogRepository
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.plugin.mongodb.aggregation.WorklogInterval
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query

class WorklogRepositoryAdapter(
    private val template: MongoTemplate,
    private val userRepositoryAdapter: UserRepositoryAdapter,
    private val worklogMongoRepository: WorklogMongoRepository
): WorklogRepository {

    override fun save(worklog: Worklog) {
        val worklogClone = WorklogClone().apply { set(worklog) }
        worklogMongoRepository.save(worklogClone)
    }

    override fun findByProjectId(projectId: String): Collection<Worklog> =
        worklogMongoRepository.findByProjectId(projectId).map { it.get() }

    override fun findByCollaboratorId(collaboratorId: String): Collection<Worklog> =
        worklogMongoRepository.findByCollaboratorId(collaboratorId).map { (it).get() }

    override fun findIntervalIdsByCollaboratorId(collaboratorId: String) =
        findWorklogIntervalIds("collaborator.\$id", collaboratorId)

    override fun findIntervalIdsByProjectId(projectId: String) = findWorklogIntervalIds("project.\$id", projectId)

    override fun findByIntervalIdOrderByLatest(intervalId: String): Collection<Worklog> =
        worklogMongoRepository.findByIntervalIdOrderByTimestampAsc(intervalId).map { it.get() }

    override fun findLatest(collaboratorId: String, projectId: String): Worklog? {
        val query = Query().apply {
            addCriteria(where("project.\$id").`is`(projectId))
            addCriteria(where("collaborator.\$id").`is`(collaboratorId))
            with(Sort(Sort.Direction.DESC, "timestamp"))
            limit(1)
        }
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

    private fun findWorklogIntervalIds(key: String, id: String): Collection<String> {
        return template.aggregate(
            createAggregation(key, id),
            Worklog::class.java,
            WorklogInterval::class.java
        ).mappedResults.map { it.intervalId }.toList()
    }

    private fun createAggregation(key: String, id: String) = newAggregation(
            match(where(key).`is`(id).andOperator(where("workStatus").`is`("START")))
        )
}
