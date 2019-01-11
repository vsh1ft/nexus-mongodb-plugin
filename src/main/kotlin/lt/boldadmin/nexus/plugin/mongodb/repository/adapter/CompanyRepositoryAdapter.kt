package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CompanyRepository
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CompanyClone

class CompanyRepositoryAdapter(private val companyMongoRepository: CompanyMongoRepository): CompanyRepository {

    override fun save(company: Company) {
        val companyClone = CompanyClone().apply { set(company) }
        companyMongoRepository.save(companyClone)

        company.id = companyClone.id
    }

    override fun existsByName(name: String) = companyMongoRepository.existsByName(name)

}