package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CollaboratorCoordinatesRepository
import lt.boldadmin.nexus.api.type.entity.CollaboratorCoordinates
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorCoordinatesMongoRepository

class CollaboratorCoordinatesRepositoryAdapter(
    private val mongoRepository: CollaboratorCoordinatesMongoRepository
): CollaboratorCoordinatesRepository {

    override fun save(collaboratorCoordinates: CollaboratorCoordinates) {
        mongoRepository.save(collaboratorCoordinates)
    }

    override fun removeByCollaboratorId(collaboratorId: String) {
        mongoRepository.removeByCollaboratorId(collaboratorId)
    }

    override fun existsByCollaboratorId(collaboratorId: String): Boolean =
        mongoRepository.existsByCollaboratorId(collaboratorId)
}
