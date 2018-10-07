package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.clone.UserClone
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserClone, String> {
    fun findByEmail(email: String): UserClone?
}
