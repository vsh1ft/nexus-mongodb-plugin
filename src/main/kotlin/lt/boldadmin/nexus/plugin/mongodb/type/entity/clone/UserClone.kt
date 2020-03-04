package lt.boldadmin.nexus.plugin.mongodb.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.Person
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.entity.Collaborator
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user")
class UserClone(

    internal var lastName: String = "",

    internal var password: String = "",

    internal var role: String = "",

    internal var companyName: String = "",

    @DBRef
    internal var collaborators: MutableCollection<Collaborator> = HashSet(),

    @DBRef
    internal var projects: MutableCollection<Project> = HashSet()

): Person() {

    internal fun set(user: User) {
        this.apply {
            id = user.id
            name = user.name
            email = user.email
            address = user.address
            companyName = user.companyName
            lastName = user.lastName
            password = user.password
            role = user.role
            user.projects.forEach { this@UserClone.projects.add(it) }
            user.collaborators.forEach { this@UserClone.collaborators.add(it) }
        }
    }

    internal fun get() = User().apply {
        id = this@UserClone.id
        name = this@UserClone.name
        email = this@UserClone.email
        address = this@UserClone.address
        companyName = this@UserClone.companyName
        lastName = this@UserClone.lastName
        password = this@UserClone.password
        role = this@UserClone.role
        this@UserClone.projects.forEach { addProject(it) }
        this@UserClone.collaborators.forEach { addCollaborator(it) }
    }
}
