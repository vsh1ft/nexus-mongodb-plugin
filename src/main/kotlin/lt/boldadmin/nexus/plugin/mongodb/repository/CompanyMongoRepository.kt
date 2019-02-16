package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CompanyClone
import org.springframework.data.mongodb.repository.MongoRepository

interface CompanyMongoRepository : MongoRepository<CompanyClone, String> {
    fun existsByName(name: String): Boolean
}
