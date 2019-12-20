package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.ProjectRepository
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectMongoRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

class ProjectRepositoryAdapter(
    private val projectMongoRepository: ProjectMongoRepository,
    private val mongoTemplate: MongoTemplate
): ProjectRepository {

    override fun save(project: Project) { projectMongoRepository.save(project) }

    override fun findById(id: String) = projectMongoRepository.findById(id).get()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Project> =
        projectMongoRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun findByCollaboratorId(id: String): Collection<Project> {
        val query = Query().addCriteria(mapOf("collaborators.\$ref" to "collaborator", "collaborators.\$id" to id))
        return mongoTemplate.findOne(query, User::class.java)?.projects ?: emptyList()
    }
}
