package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.StartedProjectWorkTokenRepository
import lt.boldadmin.nexus.api.type.entity.StartedProjectWorkToken
import lt.boldadmin.nexus.plugin.mongodb.repository.StartedProjectWorkTokenMongoRepository

class StartedProjectWorkTokenRepositoryAdapter(private val repository: StartedProjectWorkTokenMongoRepository):
    StartedProjectWorkTokenRepository {

    override fun save(workToken: StartedProjectWorkToken) {
        repository.save(workToken)
    }

    override fun findById(id: String) = repository.findById(id).get()

    override fun findByToken(token: String) = repository.findByToken(token)

    override fun existsById(id: String) = repository.existsById(id)

    override fun existsByToken(token: String) = repository.existsByToken(token)

    override fun deleteById(id: String) {
        repository.deleteById(id)
    }

}