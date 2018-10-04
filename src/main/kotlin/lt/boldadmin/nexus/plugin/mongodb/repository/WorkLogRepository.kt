package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.type.entity.WorkLogProxy
import org.springframework.data.mongodb.repository.MongoRepository

interface WorkLogRepository : MongoRepository<WorkLogProxy, String> {
    fun findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId: String, workStatus: WorkStatus)
        : WorkLog?

    fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog>

    fun findByProjectId(projectId: String): Collection<WorkLog>

    fun existsByIntervalId(intervalId: String): Boolean

    fun findByIntervalId(intervalId: String): Collection<WorkLog>

    fun findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(intervalId: String, workStatus: WorkStatus)
        : Collection<WorkLog>

    fun findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(intervalId: String, workStatus: WorkStatus): WorkLog?

    fun findFirstByIntervalId(intervalId: String): WorkLog
}