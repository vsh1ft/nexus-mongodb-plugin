package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.CompanyRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.plugin.mongodb.clone.CompanyClone
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyRepository

class CompanyRepositoryAdapter(private val companyRepository: CompanyRepository): CompanyRepositoryGateway {

    override fun findByName(name: String) = companyRepository.findByName(name)?.convertToCompany()

    override fun save(company: Company) {
        val companyClone = CompanyClone().apply { set(company) }
        companyRepository.save(companyClone)

        company.id = companyClone.id
    }

}