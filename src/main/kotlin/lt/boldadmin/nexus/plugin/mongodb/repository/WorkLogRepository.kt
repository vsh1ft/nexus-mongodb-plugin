package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.type.entity.WorkLogClone
import org.springframework.data.mongodb.repository.MongoRepository

interface WorkLogRepository : MongoRepository<WorkLogClone, String> {
    fun findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId: String, workStatus: WorkStatus)
        : WorkLogClone?

    fun findByCollaboratorId(collaboratorId: String): Collection<WorkLogClone>

    fun findByProjectId(projectId: String): Collection<WorkLogClone>

    fun existsByIntervalId(intervalId: String): Boolean

    fun findByIntervalId(intervalId: String): Collection<WorkLogClone>

    fun findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(intervalId: String, workStatus: WorkStatus)
        : Collection<WorkLogClone>

    fun findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(intervalId: String, workStatus: WorkStatus): WorkLogClone?

    fun findFirstByIntervalId(intervalId: String): WorkLogClone
}