package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.CompanyProxy
import org.springframework.data.mongodb.repository.MongoRepository

interface CompanyRepository : MongoRepository<CompanyProxy, String> {
    fun findByName(name: String): CompanyProxy?
}
