package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.type.entity.CustomerClone
import org.springframework.data.mongodb.repository.MongoRepository

interface CustomerRepository : MongoRepository<CustomerClone, String> {
     fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<CustomerClone>
}
