package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CountryRepository
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryMongodbRepository

class CountryRepositoryAdapter(private val countryMongodbRepository: CountryMongodbRepository): CountryRepository {

    override fun findAll(): Collection<Country> = countryMongodbRepository.findAll()

}