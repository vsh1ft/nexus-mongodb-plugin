package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.type.entity.CustomerProxy
import org.springframework.data.mongodb.repository.MongoRepository

interface CustomerRepository : MongoRepository<CustomerProxy, String> {
     fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer>
}
