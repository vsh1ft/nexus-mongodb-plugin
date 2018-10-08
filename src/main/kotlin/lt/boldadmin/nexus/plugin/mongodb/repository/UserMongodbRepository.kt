package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import org.springframework.data.mongodb.repository.MongoRepository

interface UserMongodbRepository : MongoRepository<UserClone, String> {
    fun findByEmail(email: String): UserClone?
}
