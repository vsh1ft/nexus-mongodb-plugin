package lt.boldadmin.nexus.plugin.mongodb.type.entity

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Customer
import org.springframework.data.mongodb.core.mapping.DBRef
import java.util.*
import javax.validation.constraints.Size

class CompanyProxy(

    var id: String? = null,

    var name: String = "",

    @DBRef(lazy = true)
    var customers: MutableCollection<Customer> = HashSet(),

    @DBRef(lazy = true)
    var collaborators: MutableCollection<Collaborator> = HashSet()

) {

    fun addCustomer(customer: Customer) {
        customers.add(customer)
    }

    fun addCollaborator(collaborator: Collaborator) {
        collaborators.add(collaborator)
    }

}
