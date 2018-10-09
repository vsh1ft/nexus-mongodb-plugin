package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.UserRepository
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongoRepository

class UserRepositoryAdapter(private val userMongoRepository: UserMongoRepository): UserRepository {

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userMongoRepository.save(userClone)

        user.id = userClone.id
    }

    override fun findAll(): Collection<User> = userMongoRepository.findAll().map { it.get() }

    override fun findById(id: String) = userMongoRepository.findById(id).get().get()

    override fun findByEmail(email: String) = userMongoRepository.findByEmail(email)?.get()

}