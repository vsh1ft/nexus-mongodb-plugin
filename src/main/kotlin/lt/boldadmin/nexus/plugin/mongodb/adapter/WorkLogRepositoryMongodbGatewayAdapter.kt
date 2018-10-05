package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.WorkLogRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.WorkLogClone

class WorkLogRepositoryMongodbGatewayAdapter(private val workLogRepository: WorkLogRepository):
    WorkLogRepositoryGateway {

    override fun findById(id: String): WorkLog {
        return getRealObject(workLogRepository.findById(id).get())!!
    }

    override fun save(workLog: WorkLog) {
        workLogRepository.save(cloneWorkLog(workLog))
    }

    override fun existsByIntervalId(intervalId: String): Boolean {
        return workLogRepository.existsByIntervalId(intervalId)
    }

    override fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog> {
        return workLogRepository.findByCollaboratorId(collaboratorId).map { getRealObject(it)!! }
    }

    override fun findByIntervalId(intervalId: String): Collection<WorkLog> {
        return workLogRepository.findByIntervalId(intervalId).map { getRealObject(it)!! }
    }

    override fun findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(
        intervalId: String, workStatus: WorkStatus
    ): Collection<WorkLog> {
        return workLogRepository.findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(intervalId, workStatus)
            .map { getRealObject(it)!! }
    }

    override fun findByProjectId(projectId: String): Collection<WorkLog> {
        return workLogRepository.findByProjectId(projectId).map { getRealObject(it)!! }
    }

    override fun findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
        collaboratorId: String, workStatus: WorkStatus
    ): WorkLog? {
        return getRealObject(
            workLogRepository.findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId, workStatus)
        )
    }

    override fun findFirstByIntervalId(intervalId: String): WorkLog {
        return getRealObject(workLogRepository.findFirstByIntervalId(intervalId))!!
    }

    override fun findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(
        intervalId: String, workStatus: WorkStatus
    ): WorkLog? {
        return getRealObject(workLogRepository.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(intervalId, workStatus))
    }

    private fun cloneWorkLog(workLog: WorkLog) =
        WorkLogClone(
            workLog.project,
            workLog.collaborator,
            workLog.timestamp,
            workLog.workStatus,
            workLog.intervalId,
            workLog.description,
            workLog.id
             )

    private fun getRealObject(workLogClone: WorkLogClone?): WorkLog? =
        if (workLogClone != null)
            WorkLog(
                workLogClone.project,
                workLogClone.collaborator,
                workLogClone.timestamp,
                workLogClone.workStatus,
                workLogClone.intervalId,
                workLogClone.description,
                workLogClone.id)
        else null

}