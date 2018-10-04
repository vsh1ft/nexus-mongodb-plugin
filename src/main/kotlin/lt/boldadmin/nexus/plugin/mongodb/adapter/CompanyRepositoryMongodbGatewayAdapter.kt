package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.CompanyRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.CompanyProxy

class CompanyRepositoryMongodbGatewayAdapter(private val companyRepository: CompanyRepository):
    CompanyRepositoryGateway {

    override fun save(company: Company) {

    }

    override fun findByName(name: String): Company? {

        return null
    }

}