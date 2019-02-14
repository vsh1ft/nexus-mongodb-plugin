package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.StartedProjectWorkToken
import org.springframework.data.mongodb.repository.MongoRepository

interface StartedProjectWorkTokenMongoRepository: MongoRepository<StartedProjectWorkToken, String> {

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): StartedProjectWorkToken

}