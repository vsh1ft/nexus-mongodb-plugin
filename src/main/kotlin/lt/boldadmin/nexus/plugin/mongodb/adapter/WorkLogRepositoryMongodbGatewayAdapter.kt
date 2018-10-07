package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.WorkLogRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogRepository
import lt.boldadmin.nexus.plugin.mongodb.clone.WorkLogClone

class WorkLogRepositoryMongodbGatewayAdapter(private val workLogRepository: WorkLogRepository):
    WorkLogRepositoryGateway {

    override fun findById(id: String): WorkLog {
        return workLogRepository.findById(id).get().convertToWorkLog()
    }

    override fun save(workLog: WorkLog) {
        workLogRepository.save(WorkLogClone().apply { set(workLog) })
    }

    override fun existsByIntervalId(intervalId: String): Boolean {
        return workLogRepository.existsByIntervalId(intervalId)
    }

    override fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog> {
        return workLogRepository.findByCollaboratorId(collaboratorId).map { (it).convertToWorkLog() }
    }

    override fun findByIntervalId(intervalId: String): Collection<WorkLog> {
        return workLogRepository.findByIntervalId(intervalId).map { it.convertToWorkLog() }
    }

    override fun findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(
        intervalId: String, workStatus: WorkStatus
    ): Collection<WorkLog> {
        return workLogRepository.findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(intervalId, workStatus)
            .map { it.convertToWorkLog() }
    }

    override fun findByProjectId(projectId: String): Collection<WorkLog> {
        return workLogRepository.findByProjectId(projectId).map { it.convertToWorkLog() }
    }

    override fun findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
        collaboratorId: String, workStatus: WorkStatus
    ): WorkLog? {
        return workLogRepository.findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId, workStatus)
                ?.convertToWorkLog()

    }

    override fun findFirstByIntervalId(intervalId: String): WorkLog {
        return workLogRepository.findFirstByIntervalId(intervalId).convertToWorkLog()
    }

    override fun findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(
        intervalId: String, workStatus: WorkStatus
    ): WorkLog? {
        return workLogRepository.findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(intervalId, workStatus)?.convertToWorkLog()
    }

}