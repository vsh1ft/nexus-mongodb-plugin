package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorklogRepository
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorklogMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorkLogClone

class WorkLogRepositoryAdapter(private val worklogMongoRepository: WorklogMongoRepository): WorklogRepository {

    override fun save(worklog: Worklog) {
        val workLogClone = WorkLogClone().apply { set(worklog) }
        worklogMongoRepository.save(workLogClone)

        worklog.id = workLogClone.id
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

}