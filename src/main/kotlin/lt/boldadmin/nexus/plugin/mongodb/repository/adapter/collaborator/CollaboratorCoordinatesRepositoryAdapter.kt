package lt.boldadmin.nexus.plugin.mongodb.repository.adapter.collaborator

import lt.boldadmin.nexus.api.repository.CollaboratorCoordinatesRepository
import lt.boldadmin.nexus.api.type.valueobject.location.CollaboratorCoordinates
import lt.boldadmin.nexus.plugin.mongodb.repository.collaborator.CollaboratorCoordinatesMongoRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.remove

class CollaboratorCoordinatesRepositoryAdapter(
    private val mongoRepository: CollaboratorCoordinatesMongoRepository,
    private val mongoTemplate: MongoTemplate
): CollaboratorCoordinatesRepository {

    override fun findByCollaboratorId(collaboratorId: String): Collection<CollaboratorCoordinates> =
        mongoRepository.findByCollaboratorIdOrderByTimestampDesc(collaboratorId)

    override fun save(collaboratorCoordinates: CollaboratorCoordinates) {
        mongoRepository.save(collaboratorCoordinates)
    }

    override fun removeOlderThan(timestamp: Long, collaboratorId: String) {
        val criteria = where("timestamp").lte(timestamp).and("collaboratorId").`is`(collaboratorId)
        mongoTemplate.remove(query(criteria), CollaboratorCoordinates::class)
    }
}
