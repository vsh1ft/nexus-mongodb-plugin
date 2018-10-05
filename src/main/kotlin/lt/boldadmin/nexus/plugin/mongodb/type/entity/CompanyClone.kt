package lt.boldadmin.nexus.plugin.mongodb.type.entity

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "company")
class CompanyClone(

    var id: String? = null,

    var name: String = "",

    @DBRef(lazy = true)
    var customers: MutableCollection<Customer> = HashSet(),

    @DBRef(lazy = true)
    var collaborators: MutableCollection<Collaborator> = HashSet()

) {

    fun setClone(company: Company) {
        this.apply {
            id = company.id
            name = company.name
            customers = company.customers
            collaborators = company.collaborators
        }
    }

    fun getRealObject() = Company().apply {
        id = this@CompanyClone.id
        name = this@CompanyClone.name
        customers = this@CompanyClone.customers
        collaborators = this@CompanyClone.collaborators
    }

}
