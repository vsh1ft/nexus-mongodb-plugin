package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.WorkLogRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.clone.WorkLogClone
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogRepository

class WorkLogRepositoryAdapter(private val workLogRepository: WorkLogRepository): WorkLogRepositoryGateway {

    override fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog> =
        workLogRepository.findByCollaboratorId(collaboratorId).map { (it).convertToWorkLog() }

    override fun existsByIntervalId(intervalId: String) = workLogRepository.existsByIntervalId(intervalId)

    override fun findById(id: String) = workLogRepository.findById(id).get().convertToWorkLog()

    override fun findByIntervalId(intervalId: String): Collection<WorkLog> =
        workLogRepository.findByIntervalId(intervalId).map { it.convertToWorkLog() }

    override fun findByProjectId(projectId: String): Collection<WorkLog> =
        workLogRepository.findByProjectId(projectId).map { it.convertToWorkLog() }

    override fun findIntervalEndpointsAsc(intervalId: String, workStatus: WorkStatus): Collection<WorkLog> =
        workLogRepository.findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(
            intervalId, workStatus
        ).map { it.convertToWorkLog() }

    override fun findFirstByIntervalId(intervalId: String) =
        workLogRepository.findFirstByIntervalId(intervalId).convertToWorkLog()

    override fun findLatestIntervalEnpointByCollaboratorId(collaboratorId: String, workStatus: WorkStatus) =
        workLogRepository.findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
            collaboratorId, workStatus
        )?.convertToWorkLog()

    override fun findLatestIntervalEnpointByIntervalId(intervalId: String, workStatus: WorkStatus) =
        workLogRepository.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(
            intervalId, workStatus
        )?.convertToWorkLog()

    override fun save(workLog: WorkLog) {
        val workLogClone = WorkLogClone().apply { set(workLog) }
        workLogRepository.save(workLogClone)

        workLog.id = workLogClone.id
    }

}