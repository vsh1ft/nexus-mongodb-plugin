package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CustomerRepository
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CustomerClone
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerMongodbRepository

class CustomerRepositoryAdapter(private val customerMongodbRepository: CustomerMongodbRepository): CustomerRepository {

    override fun findById(id: String) = customerMongodbRepository.findById(id).get().convertToCustomer()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer> =
        customerMongodbRepository
            .findByOrderNumberIsGreaterThanEqual(orderNumber)
            .map { it.convertToCustomer() }

    override fun save(customer: Customer) {
        val customerClone = CustomerClone().apply { set(customer) }
        customerMongodbRepository.save(customerClone)

        customer.id = customerClone.id
    }

}