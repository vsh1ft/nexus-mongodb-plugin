package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.ProjectRepository
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectMongodbRepository

class ProjectRepositoryAdapter(private val projectMongodbRepository: ProjectMongodbRepository): ProjectRepository {

    override fun findById(id: String) = projectMongodbRepository.findById(id).get()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Project> =
        projectMongodbRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun save(project: Project) {
        projectMongodbRepository.save(project)
    }

}