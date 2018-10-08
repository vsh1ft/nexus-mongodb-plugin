package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CompanyClone
import org.springframework.data.mongodb.repository.MongoRepository

interface CompanyMongodbRepository : MongoRepository<CompanyClone, String> {
    fun findByName(name: String): CompanyClone?
}
