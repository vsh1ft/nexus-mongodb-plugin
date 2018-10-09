package lt.boldadmin.nexus.plugin.mongodb.test.unit.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import org.junit.Test
import kotlin.test.assertEquals

class UserCloneTest {

    @Test
    fun `Sets clone`() {
        val expectedClone = createUserClone()

        val actualClone = UserClone().apply { set(createUser()) }

        assertEquals(expectedClone.id, actualClone.id)
        assertEquals(expectedClone.name, actualClone.name)
        assertEquals(expectedClone.address, actualClone.address)
        assertEquals(expectedClone.email, actualClone.email)
        assertEquals(expectedClone.company, actualClone.company)
        assertEquals(expectedClone.lastName, actualClone.lastName)
        assertEquals(expectedClone.password, actualClone.password)
        assertEquals(expectedClone.role, actualClone.role)
    }

    @Test
    fun `Converts clone to User`() {
        val expectedUser = createUser()

        val actualUser = createUserClone().get()

        assertEquals(expectedUser.id, actualUser.id)
        assertEquals(expectedUser.name, actualUser.name)
        assertEquals(expectedUser.address, actualUser.address)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.company, actualUser.company)
        assertEquals(expectedUser.lastName, actualUser.lastName)
        assertEquals(expectedUser.password, actualUser.password)
        assertEquals(expectedUser.role, actualUser.role)
    }

    private fun createUserClone() = UserClone().apply {
        id = USER_ID
        name = USER_NAME
        address = USER_ADDRESS
        email = USER_EMAIL
        company = USER_COMPANY
        lastName = USER_LAST_NAME
        password = USER_PASSWORD
        role = USER_ROLE
    }

    private fun createUser() = User().apply {
        id = USER_ID
        name = USER_NAME
        address = USER_ADDRESS
        email = USER_EMAIL
        company = USER_COMPANY
        lastName = USER_LAST_NAME
        password = USER_PASSWORD
        role = USER_ROLE
    }

    companion object {
        private val USER_ID = "USER_ID"
        private val USER_NAME = "USER_NAME"
        private val USER_ADDRESS = Address()
        private val USER_EMAIL = "EMAIL"
        private val USER_COMPANY = Company()
        private val USER_LAST_NAME = "LAST_NAME"
        private val USER_PASSWORD = "PASSWORD"
        private val USER_ROLE = "ROLE"
    }

}