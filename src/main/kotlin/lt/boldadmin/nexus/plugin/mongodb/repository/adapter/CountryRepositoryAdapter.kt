package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CountryRepository
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryMongoRepository

class CountryRepositoryAdapter(private val countryMongoRepository: CountryMongoRepository): CountryRepository {

    override fun findAll(): Collection<Country> = countryMongoRepository.findAll()

}