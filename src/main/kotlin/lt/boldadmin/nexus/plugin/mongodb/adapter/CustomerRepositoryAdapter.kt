package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.repository.CustomerRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.clone.CustomerClone
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerRepository

class CustomerRepositoryAdapter(private val customerRepository: CustomerRepository): CustomerRepositoryGateway {

    override fun findById(id: String) = customerRepository.findById(id).get().convertToCustomer()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer> =
        customerRepository
            .findByOrderNumberIsGreaterThanEqual(orderNumber)
            .map { it.convertToCustomer() }

    override fun save(customer: Customer) {
        val customerClone = CustomerClone().apply { set(customer) }
        customerRepository.save(customerClone)

        customer.id = customerClone.id
    }

}