package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CompanyRepository
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CompanyClone
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyMongodbRepository

class CompanyRepositoryAdapter(private val companyMongodbRepository: CompanyMongodbRepository): CompanyRepository {

    override fun findByName(name: String) = companyMongodbRepository.findByName(name)?.convertToCompany()

    override fun save(company: Company) {
        val companyClone = CompanyClone().apply { set(company) }
        companyMongodbRepository.save(companyClone)

        company.id = companyClone.id
    }

}