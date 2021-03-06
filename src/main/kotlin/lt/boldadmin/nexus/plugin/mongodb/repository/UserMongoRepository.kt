package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import org.springframework.data.mongodb.repository.MongoRepository

interface UserMongoRepository : MongoRepository<UserClone, String> {
    fun findByEmail(email: String): UserClone
    fun existsByEmail(email: String): Boolean
    fun existsByCompanyName(name: String): Boolean
}
