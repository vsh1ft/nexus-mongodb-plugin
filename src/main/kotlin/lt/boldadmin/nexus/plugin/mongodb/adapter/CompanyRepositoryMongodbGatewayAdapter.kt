package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.CompanyRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyRepository
import lt.boldadmin.nexus.plugin.mongodb.clone.CompanyClone

class CompanyRepositoryMongodbGatewayAdapter(private val companyRepository: CompanyRepository):
    CompanyRepositoryGateway {

    override fun save(company: Company) {
        companyRepository.save(clone(company))
    }

    override fun findByName(name: String): Company? {
        return companyRepository.findByName(name)?.convertToCompany()
    }

    private fun clone(company: Company) = CompanyClone().apply { set(company) }

}