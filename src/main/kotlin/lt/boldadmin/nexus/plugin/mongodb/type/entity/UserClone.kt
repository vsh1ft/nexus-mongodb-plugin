package lt.boldadmin.nexus.plugin.mongodb.type.entity
import lt.boldadmin.nexus.api.type.entity.*
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.Size

@Document(collection = "user")
class UserClone(

    @DBRef(lazy = true)
    var company: Company = Company(),

    var lastName: String = "",

    var password: String = "",

    var role: String = ""

) : Person() {

    fun setClone(user: User) {
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

    fun getRealObject() = User().apply {
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
