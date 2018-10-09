package lt.boldadmin.nexus.plugin.mongodb.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.*
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "customer")
class CustomerClone(

    @DBRef(lazy = true)
    internal val projects: HashSet<Project> = HashSet(),

    internal var organizationNumber: String = "",

    internal var mobileNumber: String = "",

    internal var orderNumber: Short = 0

) : Person() {

    internal fun set(customer: Customer) {
        this.apply {
            id = customer.id
            name = customer.name
            email = customer.email
            address = customer.address
            organizationNumber = customer.organizationNumber
            mobileNumber = customer.mobileNumber
            orderNumber = customer.orderNumber
            projects.apply {
                customer.projects.forEach { this@CustomerClone.addProject(it) }
            }
        }
    }

    internal fun get() = Customer().apply {
        id = this@CustomerClone.id
        name = this@CustomerClone.name
        email = this@CustomerClone.email
        address = this@CustomerClone.address
        organizationNumber = this@CustomerClone.organizationNumber
        mobileNumber = this@CustomerClone.mobileNumber
        orderNumber = this@CustomerClone.orderNumber
        projects.apply {
            this@CustomerClone.projects.forEach { addProject(it) }
        }
    }

    internal fun addProject(project: Project) {
        projects.add(project)
    }

}

