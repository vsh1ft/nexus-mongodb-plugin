package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.UserRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.repository.UserRepository
import lt.boldadmin.nexus.plugin.mongodb.clone.UserClone

class UserRepositoryMongodbGatewayAdapter(private val userRepository: UserRepository): UserRepositoryGateway {

    override fun findAll(): Collection<User> {
        return userRepository.findAll().map { it.convertToUser() }
    }

    override fun findById(id: String): User {
        return userRepository.findById(id).get().convertToUser()
    }

    override fun save(user: User) {
        userRepository.save(UserClone().apply { set(user) })
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.convertToUser()
    }

}