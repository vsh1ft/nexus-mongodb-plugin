package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.UserRepository
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongoRepository

class UserRepositoryAdapter(private val userMongoRepository: UserMongoRepository): UserRepository {

    override fun findAll(): Collection<User> = userMongoRepository.findAll().map { it.convertToUser() }

    override fun findById(id: String) = userMongoRepository.findById(id).get().convertToUser()

    override fun findByEmail(email: String) = userMongoRepository.findByEmail(email)?.convertToUser()

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userMongoRepository.save(userClone)

        user.id = userClone.id
    }

}