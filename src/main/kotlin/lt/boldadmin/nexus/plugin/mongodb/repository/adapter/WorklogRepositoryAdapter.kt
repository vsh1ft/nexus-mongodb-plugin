package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorklogRepository
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone

class WorklogRepositoryAdapter(private val worklogMongoRepository: WorklogMongoRepository,
                               private val userRepositoryAdapter: UserRepositoryAdapter): WorklogRepository {
    override fun existsByProjectIdAndCollaboratorId(projectId: String, collaboratorId: String) =
        worklogMongoRepository.existsByProjectIdAndCollaboratorId(projectId, collaboratorId)

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

    override fun findLatestByCollaboratorIdAndWorkStatusNot(collaboratorId: String, workStatus: WorkStatus) =
        worklogMongoRepository
            .findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId, workStatus)
            ?.get()

    override fun findLatestByIntervalIdAndWorkStatus(intervalId: String, workStatus: WorkStatus) =
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

    override fun findByTimestampBetweenAndProjectId (timestampGT: Long, timestampLT: Long, projectId: String)=
    worklogMongoRepository.findByTimestampBetweenAndProjectId(timestampGT, timestampLT, projectId).map {it.get()}

    override fun findByTimestampBetweenAndCollaboratorId (timestampGT: Long, timestampLT: Long, collaboratorId: String)=
        worklogMongoRepository.findByTimestampBetweenAndCollaboratorId(timestampGT, timestampLT, collaboratorId).map {it.get()}
}