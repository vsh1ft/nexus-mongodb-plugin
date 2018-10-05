package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.CustomerRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.CustomerClone

class CustomerRepositoryMongodbGatewayAdapter(private val customerRepository: CustomerRepository): CustomerRepositoryGateway {

    override fun findById(id: String): Customer {
        return customerRepository.findById(id).get().getRealObject()
    }

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer> {
        return customerRepository.findByOrderNumberIsGreaterThanEqual(orderNumber)
            .map { it.getRealObject() }
    }

    override fun save(customer: Customer) {
        customerRepository.save(CustomerClone().apply { setClone(customer) })
    }

}