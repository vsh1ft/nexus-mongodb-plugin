package lt.boldadmin.nexus.plugin.mongodb.type.entity

import lt.boldadmin.nexus.api.type.entity.Person
import lt.boldadmin.nexus.api.type.entity.Project
import org.springframework.data.mongodb.core.mapping.DBRef
import java.util.*
import javax.validation.constraints.Min

class CustomerProxy(

    @DBRef(lazy = true)
    val projects: HashSet<Project> = HashSet(),

    var organizationNumber: String = "",

    var mobileNumber: String = "",

    var orderNumber: Short = 0

) : Person() {

    fun addProject(project: Project) {
        projects.add(project)
    }

    fun incrementOrderNumber() {
        this.orderNumber++
    }
}

