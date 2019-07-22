package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.UserRepository
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone

class UserRepositoryAdapter(private val userMongoRepository: UserMongoRepository): UserRepository {

    override fun existsAny() = userMongoRepository.count() > 0

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userMongoRepository.save(userClone)
    }

    override fun findById(id: String) = userMongoRepository.findById(id).get().get()

    override fun findByEmail(email: String) = userMongoRepository.findByEmail(email).get()

    override fun existsByEmail(email: String) = userMongoRepository.existsByEmail(email)

    override fun existsByCompanyName(name: String) = userMongoRepository.existsByCompanyName(name)

    override fun findByCollaboratorId(collaboratorId: String) =
        findAll().single { user -> user.collaborators.any { it.id == collaboratorId } }

    override fun findByProjectId(projectId: String) =
        findAll().single { user -> user.projects.any { p -> p.id == projectId } }

    override fun findCollaboratorsByUserId(userId: String) = findById(userId).collaborators.toSet()

    override fun findProjectsByUserId(userId: String) = findById(userId).projects.toSet()

    override fun doesUserHaveProject(userId: String, projectId: String) =
        findById(userId).projects.any { it.id == projectId }

    override fun doesUserHaveCollaborator(userId: String, collaboratorId: String) =
        findById(userId).collaborators.any { it.id == collaboratorId }

    override fun doesUserHaveWorklog(userId: String, worklog: Worklog) =
        doesUserHaveProject(userId, worklog.project.id) && doesUserHaveCollaborator(userId, worklog.collaborator.id)

    override fun isProjectNameUnique(projectName: String, projectId: String, userId: String) =
        findProjectsByUserId(userId)
            .filter { it.id != projectId }
            .none { it.name == projectName }

    private fun findAll(): Collection<User> = userMongoRepository.findAll().map { it.get() }
}
