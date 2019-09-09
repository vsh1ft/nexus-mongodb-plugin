package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CollaboratorRepository
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorMongoRepository

class CollaboratorRepositoryAdapter(private val collaboratorMongoRepository: CollaboratorMongoRepository):
    CollaboratorRepository {

    override fun save(collaborator: Collaborator) { collaboratorMongoRepository.save(collaborator) }

    override fun findAll(): Collection<Collaborator> = collaboratorMongoRepository.findAll()

    override fun findById(id: String) = collaboratorMongoRepository.findById(id).get()

    override fun findFirstByMobileNumber(mobileNumber: String) =
        collaboratorMongoRepository.findFirstByMobileNumber(mobileNumber)

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Collaborator> =
        collaboratorMongoRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun existsById(id: String) = collaboratorMongoRepository.existsById(id)

    override fun existsByMobileNumber(mobileNumber: String) =
        collaboratorMongoRepository.existsByMobileNumber(mobileNumber)

}
