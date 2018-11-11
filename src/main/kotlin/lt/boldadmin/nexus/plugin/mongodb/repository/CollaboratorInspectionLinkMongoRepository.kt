package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.CollaboratorInspectionLink
import org.springframework.data.mongodb.repository.MongoRepository

interface CollaboratorInspectionLinkMongoRepository: MongoRepository<CollaboratorInspectionLink, String> {

    fun existsByLink(link: String): Boolean

}