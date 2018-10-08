package lt.boldadmin.nexus.plugin.mongodb.test.unit.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.adapter.UserRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.UserRepository
import lt.boldadmin.nexus.plugin.mongodb.clone.UserClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)
class UserRepositoryAdapterTest {

    @Mock
    private lateinit var userRepositorySpy: UserRepository

    private lateinit var adapter: UserRepositoryAdapter

    @Before
    fun setUp() {
        adapter = UserRepositoryAdapter(userRepositorySpy)
    }

    @Test
    fun `Saves user as a clone`() {
        val user = createUser()
        doAnswer { invocation ->
            val userClone = invocation.arguments[0] as UserClone
            userClone.apply { id = USER_ID }
        }.`when`(userRepositorySpy).save<UserClone>(any())

        adapter.save(user)

        argumentCaptor<UserClone>().apply {
            verify(userRepositorySpy).save(capture())
            assertEquals(USER_ID, firstValue.id)
            assertEquals(USER_NAME, firstValue.name)
            assertEquals(USER_ADDRESS, firstValue.address)
            assertEquals(USER_EMAIL, firstValue.email)
            assertEquals(USER_COMPANY, firstValue.company)
            assertEquals(USER_LAST_NAME, firstValue.lastName)
            assertEquals(USER_PASSWORD, firstValue.password)
            assertEquals(USER_ROLE, firstValue.role)
        }
    }

    @Test
    fun `Gets user by email`() {
        doReturn(createUserClone()).`when`(userRepositorySpy).findByEmail(USER_EMAIL)

        val actualUser = adapter.findByEmail(USER_EMAIL)

        assertUserFieldsAreEqual(actualUser!!)
    }

    @Test
    fun `Gets null if user does not exist by email`() {
        doReturn(null).`when`(userRepositorySpy).findByEmail(USER_EMAIL)

        val actualUser = adapter.findByEmail(USER_EMAIL)

        assertNull(actualUser)
    }

    @Test
    fun `Gets user by id`() {
        doReturn(Optional.of(createUserClone())).`when`(userRepositorySpy).findById(USER_ID)

        val actualUser = adapter.findById(USER_ID)

        assertUserFieldsAreEqual(actualUser)
    }

    @Test
    fun `Gets all users`() {
        val userClones = listOf(createUserClone())
        doReturn(userClones).`when`(userRepositorySpy).findAll()

        val actualUsers = adapter.findAll()

        assertUserFieldsAreEqual(actualUsers.single())
    }

    @Test
    fun `Gets that workLog exists by given interval id`() {
        val userClones = listOf(createUserClone())
        doReturn(userClones).`when`(userRepositorySpy).findAll()

        val actualUsers = adapter.findAll()

        assertUserFieldsAreEqual(actualUsers.single())
    }

    private fun assertUserFieldsAreEqual(actualUser: User) {
        assertEquals(USER_ID, actualUser.id)
        assertEquals(USER_NAME, actualUser.name)
        assertEquals(USER_ADDRESS, actualUser.address)
        assertEquals(USER_EMAIL, actualUser.email)
        assertEquals(USER_COMPANY, actualUser.company)
        assertEquals(USER_LAST_NAME, actualUser.lastName)
        assertEquals(USER_PASSWORD, actualUser.password)
        assertEquals(USER_ROLE, actualUser.role)
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
        id = null
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