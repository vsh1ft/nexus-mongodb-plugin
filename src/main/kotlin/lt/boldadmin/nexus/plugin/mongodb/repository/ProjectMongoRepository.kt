package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.Project
import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectMongoRepository : MongoRepository<Project, String> {
    fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Project>
}
