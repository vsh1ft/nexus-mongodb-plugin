package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.CustomerRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerRepository
import java.util.*

class CustomerRepositoryMongodbGatewayAdapter(private val customerRepository: CustomerRepository): CustomerRepositoryGateway {
    override fun findById(id: String): Optional<Customer> {
        return Optional.empty()
    }

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer> {
        return emptyList()
    }

    override fun save(customer: Customer) {

    }

}