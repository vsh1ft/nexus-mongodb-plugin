package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.valueobject.Country
import org.springframework.data.mongodb.repository.MongoRepository

interface CountryMongoRepository : MongoRepository<Country, String>
