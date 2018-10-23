package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.Collaborator
import org.springframework.data.mongodb.repository.MongoRepository


interface CollaboratorMongoRepository : MongoRepository<Collaborator, String> {
    fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Collaborator>

    fun findFirstByMobileNumber(mobileNumber: String): Collaborator

    fun existsByMobileNumber(mobileNumber: String): Boolean
}
