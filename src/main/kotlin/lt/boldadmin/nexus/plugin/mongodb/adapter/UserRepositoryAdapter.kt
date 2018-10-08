package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.UserRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.clone.UserClone
import lt.boldadmin.nexus.plugin.mongodb.repository.UserRepository

class UserRepositoryAdapter(private val userRepository: UserRepository): UserRepositoryGateway {

    override fun findAll(): Collection<User> = userRepository.findAll().map { it.convertToUser() }

    override fun findById(id: String) = userRepository.findById(id).get().convertToUser()

    override fun findByEmail(email: String) = userRepository.findByEmail(email)?.convertToUser()

    override fun save(user: User) {
        val userClone = UserClone().apply { set(user) }
        userRepository.save(userClone)

        user.id = userClone.id
    }

}