package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.CollaboratorRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorRepository
import java.util.*

class CollaboratorRepositoryMongodbGatewayAdapter(private val collaboratorRepository: CollaboratorRepository):
    CollaboratorRepositoryGateway {

    override fun findAll(): Collection<Collaborator> {
        return collaboratorRepository.findAll()
    }

    override fun existsById(id: String): Boolean {
        return collaboratorRepository.existsById(id)
    }

    override fun findById(id: String): Optional<Collaborator> {
        return collaboratorRepository.findById(id)
    }

    override fun existsByMobileNumber(mobileNumber: String): Boolean {
        return collaboratorRepository.existsByMobileNumber(mobileNumber)
    }

    override fun findByMobileNumber(mobileNumber: String): Collaborator {
        return collaboratorRepository.findByMobileNumber(mobileNumber)
    }

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Collaborator> {
        return collaboratorRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)
    }

    override fun save(collaborator: Collaborator) {
        collaboratorRepository.save(collaborator)
    }

}