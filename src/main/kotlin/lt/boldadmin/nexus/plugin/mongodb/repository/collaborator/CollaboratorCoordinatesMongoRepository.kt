package lt.boldadmin.nexus.plugin.mongodb.repository.collaborator

import lt.boldadmin.nexus.api.type.entity.collaborator.CollaboratorCoordinates
import org.springframework.data.mongodb.repository.MongoRepository

interface CollaboratorCoordinatesMongoRepository : MongoRepository<CollaboratorCoordinates, String> {
    fun save(collaboratorCoordinates: CollaboratorCoordinates)
    fun findByCollaboratorIdOrderByTimestampDesc(collaboratorId: String): Collection<CollaboratorCoordinates>
}
