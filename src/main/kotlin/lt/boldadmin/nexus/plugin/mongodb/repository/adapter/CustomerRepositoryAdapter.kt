package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CustomerRepository
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CustomerClone
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerMongoRepository

class CustomerRepositoryAdapter(private val customerMongoRepository: CustomerMongoRepository): CustomerRepository {

    override fun findById(id: String) = customerMongoRepository.findById(id).get().convertToCustomer()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer> =
        customerMongoRepository
            .findByOrderNumberIsGreaterThanEqual(orderNumber)
            .map { it.convertToCustomer() }

    override fun save(customer: Customer) {
        val customerClone = CustomerClone().apply { set(customer) }
        customerMongoRepository.save(customerClone)

        customer.id = customerClone.id
    }

}