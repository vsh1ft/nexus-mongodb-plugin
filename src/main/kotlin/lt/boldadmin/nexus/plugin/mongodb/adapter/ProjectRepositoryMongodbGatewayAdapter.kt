package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.ProjectRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectRepository
import java.util.*

class ProjectRepositoryMongodbGatewayAdapter(private val projectRepository: ProjectRepository):
    ProjectRepositoryGateway {

    override fun save(project: Project) {
        projectRepository.save(project)
    }

    override fun findById(id: String): Optional<Project> {
        return projectRepository.findById(id)
    }

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Project> {
        return projectRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)
    }

}