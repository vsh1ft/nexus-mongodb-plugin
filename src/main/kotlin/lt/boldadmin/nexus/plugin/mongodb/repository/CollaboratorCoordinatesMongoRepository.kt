package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.CollaboratorCoordinates
import org.springframework.data.mongodb.repository.MongoRepository

interface CollaboratorCoordinatesMongoRepository : MongoRepository<CollaboratorCoordinates, String> {
    fun save(collaboratorCoordinates: CollaboratorCoordinates)

    fun removeByCollaboratorId(collaboratorId: String)

    fun existsByCollaboratorId(collaboratorId: String): Boolean
}
