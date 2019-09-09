package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.WorklogClone
import org.springframework.data.mongodb.repository.MongoRepository

interface WorklogMongoRepository: MongoRepository<WorklogClone, String> {

    fun findByIntervalId(intervalId: String): Collection<WorklogClone>

    fun findByIntervalIdOrderByTimestampAsc(intervalId: String): Collection<WorklogClone>

    fun findFirstByCollaboratorIdOrderByTimestampDesc(collaboratorId: String): WorklogClone?

}
