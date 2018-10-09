package lt.boldadmin.nexus.plugin.mongodb.type.entity.clone
import lt.boldadmin.nexus.api.type.entity.*
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
class UserClone(

    @DBRef(lazy = true)
    internal var company: Company = Company(),

    internal var lastName: String = "",

    internal var password: String = "",

    internal var role: String = ""

) : Person() {

    internal fun set(user: User) {
        this.apply {
            id = user.id
            name = user.name
            email = user.email
            address = user.address
            company = user.company
            lastName = user.lastName
            password = user.password
            role = user.role
        }
    }

    internal fun get() = User().apply {
        id = this@UserClone.id
        name = this@UserClone.name
        email = this@UserClone.email
        address = this@UserClone.address
        company = this@UserClone.company
        lastName = this@UserClone.lastName
        password = this@UserClone.password
        role = this@UserClone.role
    }

}
