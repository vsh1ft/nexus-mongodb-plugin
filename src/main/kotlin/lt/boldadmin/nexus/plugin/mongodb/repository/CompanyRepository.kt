package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.CompanyClone
import org.springframework.data.mongodb.repository.MongoRepository

interface CompanyRepository : MongoRepository<CompanyClone, String> {
    fun findByName(name: String): CompanyClone?
}
