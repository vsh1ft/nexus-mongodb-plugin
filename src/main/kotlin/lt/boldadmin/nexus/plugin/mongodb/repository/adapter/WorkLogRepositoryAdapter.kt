package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.WorkLogRepository
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorkLogClone
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogMongodbRepository

class WorkLogRepositoryAdapter(private val workLogMongodbRepository: WorkLogMongodbRepository): WorkLogRepository {

    override fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog> =
        workLogMongodbRepository.findByCollaboratorId(collaboratorId).map { (it).convertToWorkLog() }

    override fun existsByIntervalId(intervalId: String) = workLogMongodbRepository.existsByIntervalId(intervalId)

    override fun findById(id: String) = workLogMongodbRepository.findById(id).get().convertToWorkLog()

    override fun findByIntervalId(intervalId: String): Collection<WorkLog> =
        workLogMongodbRepository.findByIntervalId(intervalId).map { it.convertToWorkLog() }

    override fun findByProjectId(projectId: String): Collection<WorkLog> =
        workLogMongodbRepository.findByProjectId(projectId).map { it.convertToWorkLog() }

    override fun findIntervalEndpointsAsc(intervalId: String, workStatus: WorkStatus): Collection<WorkLog> =
        workLogMongodbRepository.findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(
            intervalId, workStatus
        ).map { it.convertToWorkLog() }

    override fun findFirstByIntervalId(intervalId: String) =
        workLogMongodbRepository.findFirstByIntervalId(intervalId).convertToWorkLog()

    override fun findLatestIntervalEnpointByCollaboratorId(collaboratorId: String, workStatus: WorkStatus) =
        workLogMongodbRepository.findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
            collaboratorId, workStatus
        )?.convertToWorkLog()

    override fun findLatestIntervalEnpointByIntervalId(intervalId: String, workStatus: WorkStatus) =
        workLogMongodbRepository.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(
            intervalId, workStatus
        )?.convertToWorkLog()

    override fun save(workLog: WorkLog) {
        val workLogClone = WorkLogClone().apply { set(workLog) }
        workLogMongodbRepository.save(workLogClone)

        workLog.id = workLogClone.id
    }

}