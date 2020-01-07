package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.exception.NoResultException
import lt.boldadmin.nexus.api.repository.UserRepository
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

class UserRepositoryAdapter(
    private val userMongoRepository: UserMongoRepository,
    private val mongoTemplate: MongoTemplate
): UserRepository {

    override fun existsAny() = userMongoRepository.count() > 0

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userMongoRepository.save(userClone)
    }

    override fun findById(id: String) = userMongoRepository.findById(id).get().get()

    override fun findByEmail(email: String) = userMongoRepository.findByEmail(email).get()

    override fun existsByEmail(email: String) = userMongoRepository.existsByEmail(email)

    override fun existsByCompanyName(name: String) = userMongoRepository.existsByCompanyName(name)

    override fun findByProjectId(projectId: String): User {
        val query = Query().addCriteria(mapOf("projects.\$ref" to "project", "projects.\$id" to projectId))
        return mongoTemplate.findOne(query, User::class.java) ?: throw NoResultException("User was not found by given project id: $projectId")
    }

    override fun findCollaboratorsByUserId(userId: String) = findById(userId).collaborators.toSet()

    override fun findProjectsByUserId(userId: String) = findById(userId).projects.toSet()

    override fun doesUserHaveProject(userId: String, projectId: String): Boolean {
        val query = Query().addCriteria(
            mapOf("projects.\$ref" to "project", "projects.\$id" to projectId, "_id" to userId)
        )
        return mongoTemplate.exists(query, User::class.java)
    }

    override fun doesUserHaveCollaborator(userId: String, collaboratorId: String): Boolean {
        val query = Query().addCriteria(
            mapOf("collaborators.\$ref" to "collaborator", "collaborators.\$id" to collaboratorId, "_id" to userId)
        )
        return mongoTemplate.exists(query, User::class.java)
    }

    override fun doesUserHaveWorklog(userId: String, worklog: Worklog): Boolean {
        val query = Query().addCriteria(
            mapOf(
                "collaborators.\$ref" to "collaborator",
                "collaborators.\$id" to worklog.collaborator.id,
                "projects.\$ref" to "project",
                "projects.\$id" to worklog.project.id,
                "_id" to userId
            )
        )
        return mongoTemplate.exists(query, User::class.java)
    }

    override fun isProjectNameUnique(projectName: String, projectId: String, userId: String) =
        findProjectsByUserId(userId)
            .filter { it.id != projectId }
            .none { it.name == projectName }
}
