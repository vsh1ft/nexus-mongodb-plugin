package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.type.entity.UserProxy
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserProxy, String> {
    fun findByEmail(email: String): User?
}
