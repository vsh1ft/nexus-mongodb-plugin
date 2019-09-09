package lt.boldadmin.nexus.plugin.mongodb.test.unit.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import org.junit.Test
import kotlin.test.assertEquals

class UserCloneTest {

    @Test
    fun `Sets user clone`() {
        val expectedClone = createUserClone()

        val actualClone = UserClone().apply { set(createUser()) }

        assertEquals(expectedClone.id, actualClone.id)
        assertEquals(expectedClone.name, actualClone.name)
        assertEquals(expectedClone.address, actualClone.address)
        assertEquals(expectedClone.email, actualClone.email)
        assertEquals(expectedClone.companyName, actualClone.companyName)
        assertEquals(expectedClone.lastName, actualClone.lastName)
        assertEquals(expectedClone.password, actualClone.password)
        assertEquals(expectedClone.role, actualClone.role)
        assertEquals(expectedClone.projects.first(), actualClone.projects.first())
        assertEquals(expectedClone.collaborators.first(), actualClone.collaborators.first())
    }

    @Test
    fun `Converts clone to User`() {
        val expectedUser = createUser()

        val actualUser = createUserClone().get()

        assertEquals(expectedUser.id, actualUser.id)
        assertEquals(expectedUser.name, actualUser.name)
        assertEquals(expectedUser.address, actualUser.address)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.companyName, actualUser.companyName)
        assertEquals(expectedUser.lastName, actualUser.lastName)
        assertEquals(expectedUser.password, actualUser.password)
        assertEquals(expectedUser.role, actualUser.role)
        assertEquals(expectedUser.projects.first(), actualUser.projects.first())
        assertEquals(expectedUser.collaborators.first(), actualUser.collaborators.first())
    }

    private fun createUserClone() = UserClone().apply {
        id = USER_ID
        name = USER_NAME
        address = USER_ADDRESS
        email = USER_EMAIL
        companyName = USER_COMPANY_NAME
        lastName = USER_LAST_NAME
        password = USER_PASSWORD
        role = USER_ROLE
        projects = PROJECTS
        collaborators = COLLABORATORS
    }

    private fun createUser() = User().apply {
        id = USER_ID
        name = USER_NAME
        address = USER_ADDRESS
        email = USER_EMAIL
        companyName = USER_COMPANY_NAME
        lastName = USER_LAST_NAME
        password = USER_PASSWORD
        role = USER_ROLE
        projects = PROJECTS
        collaborators = COLLABORATORS
    }

    companion object {
        private val USER_ID = "USER_ID"
        private val USER_NAME = "USER_NAME"
        private val USER_ADDRESS = Address()
        private val USER_EMAIL = "EMAIL"
        private val USER_LAST_NAME = "LAST_NAME"
        private val USER_PASSWORD = "PASSWORD"
        private val USER_ROLE = "ROLE"
        private val USER_COMPANY_NAME = "USER_COMPANY_NAME"
        private val COLLABORATORS = mutableListOf(Collaborator())
        private val PROJECTS = mutableListOf(Project())
    }

}
