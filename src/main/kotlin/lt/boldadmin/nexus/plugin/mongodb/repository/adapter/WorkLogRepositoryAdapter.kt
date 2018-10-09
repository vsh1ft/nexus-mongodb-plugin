package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorkLogRepository
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorkLogClone
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogMongoRepository

class WorkLogRepositoryAdapter(private val workLogMongoRepository: WorkLogMongoRepository): WorkLogRepository {

    override fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog> =
        workLogMongoRepository.findByCollaboratorId(collaboratorId).map { (it).convertToWorkLog() }

    override fun existsByIntervalId(intervalId: String) = workLogMongoRepository.existsByIntervalId(intervalId)

    override fun findById(id: String) = workLogMongoRepository.findById(id).get().convertToWorkLog()

    override fun findByIntervalId(intervalId: String): Collection<WorkLog> =
        workLogMongoRepository.findByIntervalId(intervalId).map { it.convertToWorkLog() }

    override fun findByProjectId(projectId: String): Collection<WorkLog> =
        workLogMongoRepository.findByProjectId(projectId).map { it.convertToWorkLog() }

    override fun findIntervalEndpointsAsc(intervalId: String, workStatus: WorkStatus): Collection<WorkLog> =
        workLogMongoRepository.findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(
            intervalId, workStatus
        ).map { it.convertToWorkLog() }

    override fun findFirstByIntervalId(intervalId: String) =
        workLogMongoRepository.findFirstByIntervalId(intervalId).convertToWorkLog()

    override fun findLatestIntervalEnpointByCollaboratorId(collaboratorId: String, workStatus: WorkStatus) =
        workLogMongoRepository.findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
            collaboratorId, workStatus
        )?.convertToWorkLog()

    override fun findLatestIntervalEnpointByIntervalId(intervalId: String, workStatus: WorkStatus) =
        workLogMongoRepository.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(
            intervalId, workStatus
        )?.convertToWorkLog()

    override fun save(workLog: WorkLog) {
        val workLogClone = WorkLogClone().apply { set(workLog) }
        workLogMongoRepository.save(workLogClone)

        workLog.id = workLogClone.id
    }

}