package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.springframework.data.mongodb.repository.MongoRepository

interface WorklogMongoRepository : MongoRepository<WorklogClone, String> {
    fun findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(collaboratorId: String, workStatus: WorkStatus)
        : WorklogClone?

    fun findByCollaboratorId(collaboratorId: String): Collection<WorklogClone>

    fun findByProjectId(projectId: String): Collection<WorklogClone>

    fun findByIntervalId(intervalId: String): Collection<WorklogClone>

    fun findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(intervalId: String, workStatus: WorkStatus)
        : Collection<WorklogClone>

    fun findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(intervalId: String, workStatus: WorkStatus): WorklogClone?

    fun findFirstByIntervalId(intervalId: String): WorklogClone

    fun existsByIntervalId(intervalId: String): Boolean

    fun existsByProjectIdAndCollaboratorId(projectId: String, collaboratorId: String): Boolean

    fun findByTimestampBetweenAndProjectId (timestampGT: Long, timestampLT: Long, projectId: String)
        : Collection<WorklogClone>

    fun findByTimestampBetweenAndCollaboratorId (timestampGT: Long, timestampLT: Long, collaboratorId: String)
        : Collection<WorklogClone>

}