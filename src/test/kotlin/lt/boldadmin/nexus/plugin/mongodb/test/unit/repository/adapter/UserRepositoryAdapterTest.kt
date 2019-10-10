package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockitokotlin2.*
import lt.boldadmin.nexus.api.type.entity.*
import lt.boldadmin.nexus.api.type.valueobject.*
import lt.boldadmin.nexus.plugin.mongodb.repository.UserMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.UserRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.UserClone
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserRepositoryAdapterTest {

    @Mock
    private lateinit var userMongoRepositorySpy: UserMongoRepository

    private lateinit var adapter: UserRepositoryAdapter

    @BeforeEach
    fun setUp() {
        adapter = UserRepositoryAdapter(userMongoRepositorySpy)
    }

    @Test
    fun `Saves user as a clone`() {
        val user = TypeFactory().createUser()
        doAnswer { invocation ->
            val userClone = invocation.arguments[0] as UserClone
            userClone.apply { id = USER_ID }
        }.`when`(userMongoRepositorySpy).save<UserClone>(any())

        adapter.save(user)

        argumentCaptor<UserClone>().apply {
            verify(userMongoRepositorySpy).save(capture())
            assertEquals(user.id, firstValue.id)
            assertEquals(user.name, firstValue.name)
            assertEquals(user.address, firstValue.address)
            assertEquals(user.email, firstValue.email)
            assertEquals(user.companyName, firstValue.companyName)
            assertEquals(user.lastName, firstValue.lastName)
            assertEquals(user.password, firstValue.password)
            assertEquals(user.role, firstValue.role)
            assertEquals(user.projects.first(), firstValue.projects.first())
            assertEquals(user.collaborators.first(), firstValue.collaborators.first())
        }
    }

    @Test
    fun `Gets user by email`() {
        val expectedUser = TypeFactory().createUserClone()
        doReturn(expectedUser).`when`(userMongoRepositorySpy).findByEmail(USER_EMAIL)

        val actualUser = adapter.findByEmail(USER_EMAIL)

        assertUserFieldsAreEqual(expectedUser.get(), actualUser)
    }

    @Test
    fun `User exist by email`() {
        doReturn(true).`when`(userMongoRepositorySpy).existsByEmail(USER_EMAIL)

        val actualExists = adapter.existsByEmail(USER_EMAIL)

        assertTrue(actualExists)
    }

    @Test
    fun `User exist by company name`() {
        doReturn(true).`when`(userMongoRepositorySpy).existsByCompanyName(USER_COMPANY_NAME)

        val actualExists = adapter.existsByCompanyName(USER_COMPANY_NAME)

        assertTrue(actualExists)
    }

    @Test
    fun `Gets user by id`() {
        val expectedUser = TypeFactory().createUserClone()
        doReturn(Optional.of(expectedUser)).`when`(userMongoRepositorySpy).findById(USER_ID)

        val actualUser = adapter.findById(USER_ID)

        assertUserFieldsAreEqual(expectedUser.get(), actualUser)
    }

    @Test
    fun `Exists any when count is greater than zero`() {
        doReturn(1L).`when`(userMongoRepositorySpy).count()

        val actualExists = adapter.existsAny()

        assertTrue(actualExists)
    }

    @Test
    fun `Does not exist when count is equal to zero`() {
        doReturn(0L).`when`(userMongoRepositorySpy).count()

        val actualExists = adapter.existsAny()

        assertFalse(actualExists)
    }

    @Test
    fun `User has project when ids match`() {
        doReturn(Optional.of(TypeFactory().createUserClone())).`when`(userMongoRepositorySpy).findById(USER_ID)

        val doesUserHaveProject = adapter.doesUserHaveProject(USER_ID, PROJECT_ID)

        assertTrue(doesUserHaveProject)
    }

    @Test
    fun `User doesn't have project when ids don't match`() {
        doReturn(Optional.of(TypeFactory().createUserClone())).`when`(userMongoRepositorySpy).findById(USER_ID)

        val doesUserHaveProject = adapter.doesUserHaveProject(USER_ID, "otherId")

        assertFalse(doesUserHaveProject)
    }

    @Test
    fun `User has collaborator when ids match`() {
        doReturn(Optional.of(TypeFactory().createUserClone())).`when`(userMongoRepositorySpy).findById(USER_ID)

        val doesUserHaveCollaborator = adapter.doesUserHaveCollaborator(USER_ID, COLLABORATOR_ID)

        assertTrue(doesUserHaveCollaborator)
    }

    @Test
    fun `User doesn't have collaborator when ids don't match`() {
        doReturn(Optional.of(TypeFactory().createUserClone())).`when`(userMongoRepositorySpy).findById(USER_ID)

        val doesUserHaveCollaborator = adapter.doesUserHaveCollaborator(USER_ID, "otherId")

        assertFalse(doesUserHaveCollaborator)
    }

    @Test
    fun `Finds by Project id`() {
        val expectedUser = TypeFactory().createUserClone()
        doReturn(listOf(expectedUser)).`when`(userMongoRepositorySpy).findAll()

        val actualUser = adapter.findByProjectId(PROJECT_ID)

        assertUserFieldsAreEqual(expectedUser.get(), actualUser)
    }

    @Test
    fun `Doesn't find by Project id`() {
        doReturn(listOf(TypeFactory().createUserClone())).`when`(userMongoRepositorySpy).findAll()

        assertThrows(NoSuchElementException::class.java) {
            adapter.findByProjectId("otherId")
        }
    }

    @Test
    fun `Project name is not unique among User Projects`() {
        val userClone = UserClone(projects = mutableListOf(Project("other id", PROJECT_NAME)))
        doReturn(Optional.of(userClone)).`when`(userMongoRepositorySpy).findById(USER_ID)

        val isProjectNameUnique = adapter.isProjectNameUnique(PROJECT_NAME, PROJECT_ID, USER_ID)

        assertFalse(isProjectNameUnique)
    }

    @Test
    fun `Project name is unique among User Projects`() {
        val userClone = UserClone(projects = mutableListOf(Project("other id", PROJECT_NAME)))

        doReturn(Optional.of(userClone)).`when`(userMongoRepositorySpy).findById(USER_ID)

        val isProjectNameUnique = adapter.isProjectNameUnique("new name", PROJECT_ID, USER_ID)

        assertTrue(isProjectNameUnique)
    }

    @Test
    fun `Project name is unique if the search is made against Project with the same name`() {
        val user = TypeFactory().createUserClone()
        doReturn(Optional.of(user)).`when`(userMongoRepositorySpy).findById(USER_ID)

        val isProjectNameUnique = adapter.isProjectNameUnique(PROJECT_NAME, PROJECT_ID, USER_ID)

        assertTrue(isProjectNameUnique)
    }

    @Test
    fun `Finds User Projects`() {
        val user = TypeFactory().createUserClone()
        doReturn(Optional.of(user)).`when`(userMongoRepositorySpy).findById(USER_ID)

        val projects = adapter.findProjectsByUserId(USER_ID)

        assertEquals(1, projects.size)
        assertEquals(PROJECT_NAME, projects.first().name)
    }

    @Test
    fun `Finds User Collaborators`() {
        val user = TypeFactory().createUserClone()
        doReturn(Optional.of(user)).`when`(userMongoRepositorySpy).findById(USER_ID)

        val collaborators = adapter.findCollaboratorsByUserId(USER_ID)

        assertEquals(1, collaborators.size)
        assertEquals(COLLABORATOR_ID, collaborators.first().id)
    }

    @Test
    fun `Finds by collaborator id`() {
        val expectedUser = TypeFactory().createUserClone()
        doReturn(listOf(expectedUser)).`when`(userMongoRepositorySpy).findAll()

        val actualUser = adapter.findByCollaboratorId(COLLABORATOR_ID)

        assertUserFieldsAreEqual(expectedUser.get(), actualUser)
    }

    @Test
    fun `Doesn't find by collaborator id`() {
        doReturn(listOf(TypeFactory().createUserClone())).`when`(userMongoRepositorySpy).findAll()

        assertThrows(NoSuchElementException::class.java) {
            adapter.findByCollaboratorId("otherId")
        }
    }

    @Test
    fun `User has worklog when collaborator or project ids match by interval id`() {
        doReturn(Optional.of(TypeFactory().createUserClone()))
            .`when`(userMongoRepositorySpy).findById(USER_ID)

        val worklog = TypeFactory().createWorklog("start", WorkStatus.START)
        val hasWorklog = adapter.doesUserHaveWorklog(USER_ID, worklog)
        assertTrue(hasWorklog)
    }

    @Test
    fun `User does not have worklog when project or collaborator ids don't match by interval id`() {
        doReturn(Optional.of(TypeFactory().createUserClone()))
            .`when`(userMongoRepositorySpy).findById(USER_ID)
        val worklog = TypeFactory().createWorklog("start", WorkStatus.START).apply {
            this.project.id = "otherId"
            this.collaborator.id = "otherId"
        }

        val hasWorklog = adapter.doesUserHaveWorklog(USER_ID, worklog)
        assertFalse(hasWorklog)
    }

    @Test
    fun `User does not have worklog when project ids don't match by interval id`() {
        doReturn(Optional.of(TypeFactory().createUserClone()))
            .`when`(userMongoRepositorySpy).findById(USER_ID)
        val worklog = TypeFactory().createWorklog("start", WorkStatus.START).apply {
            this.project.id = "otherId"
        }
        val hasWorklog = adapter.doesUserHaveWorklog(USER_ID, worklog)

        assertFalse(hasWorklog)
    }

    @Test
    fun `User does not have worklog when collaborator ids don't match by interval id`() {
        doReturn(Optional.of(TypeFactory().createUserClone()))
            .`when`(userMongoRepositorySpy).findById(USER_ID)
        val worklog = TypeFactory().createWorklog("start", WorkStatus.START).apply {
            this.collaborator.id = "otherId"
        }

        val hasWorklog = adapter.doesUserHaveWorklog(USER_ID, worklog)

        assertFalse(hasWorklog)
    }

    private fun assertUserFieldsAreEqual(expectedUser: User, actualUser: User) {
        assertEquals(expectedUser.id, actualUser.id)
        assertEquals(expectedUser.name, actualUser.name)
        assertEquals(expectedUser.address, actualUser.address)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.companyName, actualUser.companyName)
        assertEquals(expectedUser.lastName, actualUser.lastName)
        assertEquals(expectedUser.password, actualUser.password)
        assertEquals(expectedUser.role, actualUser.role)
        assertEquals(expectedUser.projects.first().id, actualUser.projects.first().id)
        assertEquals(expectedUser.collaborators.first().id, actualUser.collaborators.first().id)
        assertEquals(1, actualUser.projects.size)
        assertEquals(1, actualUser.collaborators.size)
    }

    companion object {
        private val USER_ID = "USER_ID"
        private val USER_ADDRESS = Address()
        private val USER_EMAIL = "EMAIL"
        private val USER_PASSWORD = "PASSWORD"
        private val USER_COMPANY_NAME = "USER_COMPANY_NAME"

        private val COLLABORATOR_ID = "COLLABORATOR_ID"
        private val PROJECT_ID = "PROJECT_ID"
        private val PROJECT_NAME = "PROJECT_NAME"
        private val PROJECT_LOCATION = Location(Coordinates(1.1, 2.2))
    }

    inner class TypeFactory {
        fun createUserClone() = UserClone().apply {
            id = USER_ID
            name = "USER_NAME"
            address = USER_ADDRESS
            email = USER_EMAIL
            lastName = "LAST_NAME"
            password = USER_PASSWORD
            role = "ROLE"
            projects = mutableListOf(createProject())
            collaborators = mutableListOf(createCollaborator())
        }

        fun createUser() = User().apply {
            id = USER_ID
            name = "USER_NAME"
            address = USER_ADDRESS
            email = USER_EMAIL
            lastName = "LAST_NAME"
            password = USER_PASSWORD
            role = "ROLE"
            companyName = USER_COMPANY_NAME
            projects = mutableListOf(createProject())
            collaborators = mutableListOf(createCollaborator())
        }

        private fun createProject() = Project(PROJECT_ID, PROJECT_NAME, PROJECT_LOCATION)

        private fun createCollaborator() = Collaborator().apply { id = COLLABORATOR_ID }

        fun createWorklog(worklogId: String = "", workStatus: WorkStatus = WorkStatus.START) =
            Worklog(
                createProject(),
                createCollaborator(),
                0,
                workStatus,
                "INTERVAL_ID",
                worklogId
            )
    }
}
