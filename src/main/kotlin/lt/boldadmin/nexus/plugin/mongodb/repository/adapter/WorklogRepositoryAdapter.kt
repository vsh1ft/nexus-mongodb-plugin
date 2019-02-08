package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorklogRepository
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query


class WorklogRepositoryAdapter(
    private val template: MongoTemplate,
    private val userRepositoryAdapter: UserRepositoryAdapter,
    private val worklogMongoRepository: WorklogMongoRepository
): WorklogRepository {

    override fun save(worklog: Worklog) {
        val worklogClone = WorklogClone().apply { set(worklog) }
        worklogMongoRepository.save(worklogClone)

        worklog.id = worklogClone.id
    }

    override fun findById(id: String) = worklogMongoRepository.findById(id).get().get()

    override fun findByProjectId(projectId: String): Collection<Worklog> =
        worklogMongoRepository.findByProjectId(projectId).map { it.get() }

    override fun findByIntervalId(intervalId: String): Collection<Worklog> =
        worklogMongoRepository.findByIntervalId(intervalId).map { it.get() }

    override fun findByCollaboratorId(collaboratorId: String): Collection<Worklog> =
        worklogMongoRepository.findByCollaboratorId(collaboratorId).map { (it).get() }

    override fun findFirstByIntervalId(intervalId: String) =
        worklogMongoRepository.findFirstByIntervalId(intervalId).get()

    override fun findByIntervalIdAndWorkStatusNotOrderByLatest(intervalId: String, workStatus: WorkStatus):
        Collection<Worklog> =
        worklogMongoRepository
            .findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(intervalId, workStatus)
            .map { it.get() }

    override fun findLatestWithWorkStatusNot(collaboratorId: String, workStatus: WorkStatus) =
        worklogMongoRepository
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId, workStatus)
            ?.get()

    override fun findLatestWithWorkStatusNot(
        projectId: String,
        collaboratorId: String,
        workStatus: WorkStatus
    ): Worklog? {
        val query = Query().apply {
            addCriteria(Criteria.where("project.\$id").`is`(ObjectId(projectId)))
            addCriteria(Criteria.where("collaborator.\$id").`is`(ObjectId(collaboratorId)))
            addCriteria(Criteria.where("workStatus").ne(workStatus))
            with(Sort(Sort.Direction.DESC, "timestamp"))
            limit(1)
        }
        return template.findOne(query, Worklog::class.java)
    }

    override fun findLatest(intervalId: String, workStatus: WorkStatus) =
        worklogMongoRepository
            .findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(intervalId, workStatus)
            ?.get()

    override fun existsByIntervalId(intervalId: String) = worklogMongoRepository.existsByIntervalId(intervalId)

    override fun doesUserHaveWorklogInterval(userId: String, intervalId: String) =
        findByIntervalId(intervalId).all { userRepositoryAdapter.doesUserHaveWorklog(userId, it) }

    override fun doesCollaboratorHaveWorklogInterval(collaboratorId: String, intervalId: String) =
        findByIntervalId(intervalId).all { it.collaborator.id == collaboratorId }

    override fun doesCollaboratorHaveWorklogIntervals(collaboratorId: String, intervalIds: Collection<String>) =
        intervalIds.all { doesCollaboratorHaveWorklogInterval(collaboratorId, it) }

}
