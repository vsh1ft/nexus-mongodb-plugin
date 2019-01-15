package lt.boldadmin.nexus.plugin.mongodb.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "company")
class CompanyClone(

    internal var id: String = "",

    internal var name: String = "",

    @DBRef(lazy = true)
    internal var customers: MutableCollection<Customer> = HashSet(),

    @DBRef(lazy = true)
    internal var collaborators: MutableCollection<Collaborator> = HashSet()

) {

    internal fun set(company: Company) {
        this.apply {
            id = company.id
            name = company.name
            customers = company.customers
            collaborators = company.collaborators
        }
    }

    internal fun get() = Company().apply {
        id = this@CompanyClone.id
        name = this@CompanyClone.name
        customers = this@CompanyClone.customers
        collaborators = this@CompanyClone.collaborators
    }

}
