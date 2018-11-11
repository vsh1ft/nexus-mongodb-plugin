package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CollaboratorInspectionLinkRepository
import lt.boldadmin.nexus.api.type.entity.CollaboratorInspectionLink
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorInspectionLinkMongoRepository

class CollaboratorInspectionLinkRepositoryAdapter (private val repository: CollaboratorInspectionLinkMongoRepository):
    CollaboratorInspectionLinkRepository {

    override fun existsById(id: String) = repository.existsById(id)

    override fun deleteById(id: String) {
        repository.deleteById(id)
    }

    override fun existsByLink(link: String) = repository.existsByLink(link)


    override fun findById(id: String) = repository.findById(id).get()

    override fun save(inspectionLink: CollaboratorInspectionLink) {
        repository.save(inspectionLink)
    }

}