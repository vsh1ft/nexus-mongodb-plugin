package lt.boldadmin.nexus.plugin.mongodb.type.entity
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.api.type.entity.Person
import org.springframework.data.mongodb.core.mapping.DBRef
import javax.validation.constraints.Size

class UserProxy(

    @DBRef(lazy = true)
    var company: Company = Company(),

    var lastName: String = "",

    var password: String = "",

    var role: String = ""

) : Person() {

    fun getCustomers(): Collection<Customer> = company.customers

}
