package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CollaboratorRepository
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorMongodbRepository

class CollaboratorRepositoryAdapter(private val collaboratorMongodbRepository: CollaboratorMongodbRepository):
    CollaboratorRepository {

    override fun existsById(id: String) = collaboratorMongodbRepository.existsById(id)

    override fun existsByMobileNumber(mobileNumber: String) = collaboratorMongodbRepository.existsByMobileNumber(mobileNumber)

    override fun findAll(): Collection<Collaborator> = collaboratorMongodbRepository.findAll()

    override fun findById(id: String) = collaboratorMongodbRepository.findById(id).get()

    override fun findByMobileNumber(mobileNumber: String) = collaboratorMongodbRepository.findByMobileNumber(mobileNumber)

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Collaborator> =
        collaboratorMongodbRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)

    override fun save(collaborator: Collaborator) {
        collaboratorMongodbRepository.save(collaborator)
    }

}