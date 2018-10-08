package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.UserRepository
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongodbRepository

class UserRepositoryAdapter(private val userMongodbRepository: UserMongodbRepository): UserRepository {

    override fun findAll(): Collection<User> = userMongodbRepository.findAll().map { it.convertToUser() }

    override fun findById(id: String) = userMongodbRepository.findById(id).get().convertToUser()

    override fun findByEmail(email: String) = userMongodbRepository.findByEmail(email)?.convertToUser()

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userMongodbRepository.save(userClone)

        user.id = userClone.id
    }

}