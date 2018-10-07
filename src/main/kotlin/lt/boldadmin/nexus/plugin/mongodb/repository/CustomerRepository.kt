package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.clone.CustomerClone
import org.springframework.data.mongodb.repository.MongoRepository

interface CustomerRepository : MongoRepository<CustomerClone, String> {
     fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<CustomerClone>
}
