package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.type.entity.UserClone
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserClone, String> {
    fun findByEmail(email: String): UserClone?
}
