package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.ProjectRepository
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectMongoRepository

class ProjectRepositoryAdapter(private val projectMongoRepository: ProjectMongoRepository): ProjectRepository {

    override fun findById(id: String) = projectMongoRepository.findById(id).get()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Project> =
        projectMongoRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun save(project: Project) {
        projectMongoRepository.save(project)
    }

}