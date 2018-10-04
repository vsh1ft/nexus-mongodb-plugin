package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.UserRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.plugin.mongodb.repository.UserRepository
import java.util.*

class UserRepositoryMongodbGatewayAdapter(private val userRepository: UserRepository): UserRepositoryGateway {

    override fun findAll(): Collection<User> {
        return emptyList()
    }

    override fun findById(id: String): Optional<User> {
        return Optional.empty()
    }

    override fun save(user: User) {
    }

    override fun findByEmail(email: String): User? {
        return null
    }

}