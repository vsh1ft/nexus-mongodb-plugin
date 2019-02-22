package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import lt.boldadmin.nexus.api.repository.CustomerRepository
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CustomerClone

class CustomerRepositoryAdapter(private val customerMongoRepository: CustomerMongoRepository): CustomerRepository {

    override fun save(customer: Customer) {
        val customerClone = CustomerClone().apply { set(customer) }
        customerMongoRepository.save(customerClone)
    }

    override fun findById(id: String) = customerMongoRepository.findById(id).get().get()

    override fun findByOrderNumberIsGreaterThanEqual(orderNumber: Short): Collection<Customer> =
        customerMongoRepository
            .findByOrderNumberIsGreaterThanEqual(orderNumber)
            .map { it.get() }

}