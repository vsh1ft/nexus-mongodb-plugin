package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.CollaboratorRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorRepository

class CollaboratorRepositoryAdapter(private val collaboratorRepository: CollaboratorRepository):
    CollaboratorRepositoryGateway {

    override fun existsById(id: String) = collaboratorRepository.existsById(id)

    override fun existsByMobileNumber(mobileNumber: String) = collaboratorRepository.existsByMobileNumber(mobileNumber)

    override fun findAll(): Collection<Collaborator> = collaboratorRepository.findAll()

    override fun findById(id: String) = collaboratorRepository.findById(id).get()

    override fun findByMobileNumber(mobileNumber: String) = collaboratorRepository.findByMobileNumber(mobileNumber)

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Collaborator> =
        collaboratorRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun save(collaborator: Collaborator) {
        collaboratorRepository.save(collaborator)
    }

}