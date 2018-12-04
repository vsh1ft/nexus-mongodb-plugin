package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.UserRepository
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone

class UserRepositoryAdapter(private val userMongoRepository: UserMongoRepository): UserRepository {

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userMongoRepository.save(userClone)

        user.id = userClone.id
    }

    override fun findAll(): Collection<User> = userMongoRepository.findAll().map { it.get() }

    override fun findById(id: String) = userMongoRepository.findById(id).get().get()

    override fun findByEmail(email: String) = userMongoRepository.findByEmail(email)?.get()

    override fun doesUserHaveCustomer(userId: String, customerId: String) =
        getCustomers(userId).any { it.id == customerId }

    override fun doesUserHaveProject(userId: String, projectId: String) =
        getCustomers(userId)
            .flatMap { it.projects }
            .any { it.id == projectId }

    override fun doesUserHaveCollaborator(userId: String, collaboratorId: String) =
        findById(userId)
            .company
            .collaborators
            .any { it.id == collaboratorId }

    override fun doesUserHaveWorklog(userId: String, worklog: Worklog) =
        doesUserHaveProject(userId, worklog.project.id!!) &&
            doesUserHaveCollaborator(userId, worklog.collaborator.id!!)

    override fun findByCollaboratorId(collaboratorId: String) =
        findAll().single { user -> user.company.collaborators.any { it.id == collaboratorId } }

    override fun findByProjectId(projectId: String) =
        findAll().single { it.company.customers.any { it.projects.any { it.id == projectId } } }

    override fun isProjectNameUnique(projectName: String, projectId: String, userId: String) =
        findProjectByUserId(userId)
            .filter { it.id != projectId }
            .none { it.name == projectName }

    private fun getCustomers(userId: String) =
        findById(userId)
            .company
            .customers

    override fun findProjectByUserId(userId: String)=
        findById(userId)
            .company
            .customers
            .flatMap { it.projects }
            .toSet()

}