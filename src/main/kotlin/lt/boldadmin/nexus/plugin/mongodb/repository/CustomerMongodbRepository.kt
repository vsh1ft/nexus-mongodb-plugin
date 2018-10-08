package lt.boldadmin.nexus.plugin.mongodb.repository

import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CustomerClone
import org.springframework.data.mongodb.repository.MongoRepository

interface CustomerMongodbRepository : MongoRepository<CustomerClone, String> {
     fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<CustomerClone>
}
