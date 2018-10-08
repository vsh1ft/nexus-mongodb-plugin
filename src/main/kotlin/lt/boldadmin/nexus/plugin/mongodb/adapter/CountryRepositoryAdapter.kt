package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.CountryRepositoryGateway
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryRepository

class CountryRepositoryAdapter(private val countryRepository: CountryRepository): CountryRepositoryGateway {

    override fun findAll(): Collection<Country> = countryRepository.findAll()

}