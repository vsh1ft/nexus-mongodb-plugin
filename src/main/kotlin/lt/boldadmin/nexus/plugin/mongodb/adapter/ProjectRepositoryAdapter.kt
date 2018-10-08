package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.ProjectRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectRepository

class ProjectRepositoryAdapter(private val projectRepository: ProjectRepository): ProjectRepositoryGateway {

    override fun findById(id: String) = projectRepository.findById(id).get()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Project> =
        projectRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun save(project: Project) {
        projectRepository.save(project)
    }

}