package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.api.type.valueobject.TimeRange
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CollaboratorRepositoryAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class CollaboratorRepositoryAdapterTest {

    @Mock
    private lateinit var collaboratorMongoRepositorySpy: CollaboratorMongoRepository

    private lateinit var adapter: CollaboratorRepositoryAdapter

    @Before
    fun setUp() {
        adapter = CollaboratorRepositoryAdapter(collaboratorMongoRepositorySpy)
    }

    @Test
    fun `Saves collaborator`() {
        val collaborator = createCollaborator()

        adapter.save(collaborator)

        verify(collaboratorMongoRepositorySpy).save(collaborator)
    }

    @Test
    fun `Gets collaborator by id`() {
        val expectedCollaborator = createCollaborator()
        doReturn(Optional.of(expectedCollaborator)).`when`(collaboratorMongoRepositorySpy).findById(COLLABORATOR_ID)

        val actualCollaborator = adapter.findById(COLLABORATOR_ID)

        assertSame(expectedCollaborator, actualCollaborator)
    }

    @Test
    fun `Confirms that collaborator exists by mobile number`() {
        doReturn(true).`when`(collaboratorMongoRepositorySpy).existsByMobileNumber(COLLABORATOR_NUMBER)

        val exists = adapter.existsByMobileNumber(COLLABORATOR_NUMBER)

        assertTrue(exists)
    }

    @Test
    fun `Confirms that collaborator exists by id`() {
        doReturn(true).`when`(collaboratorMongoRepositorySpy).existsById(COLLABORATOR_ID)

        val exists = adapter.existsById(COLLABORATOR_ID)

        assertTrue(exists)
    }

    @Test
    fun `Gets collaborator by mobile number`() {
        val expectedCollaborator = createCollaborator()
        doReturn(expectedCollaborator).`when`(collaboratorMongoRepositorySpy)
            .findFirstByMobileNumber(COLLABORATOR_NUMBER)

        val actualCollaborator = adapter.findFirstByMobileNumber(COLLABORATOR_NUMBER)

        assertSame(expectedCollaborator, actualCollaborator)
    }

    @Test
    fun `Gets all collaborators`() {
        val expectedCollaborators = listOf(createCollaborator())
        doReturn(expectedCollaborators).`when`(collaboratorMongoRepositorySpy).findAll()

        val actualCollaborators = adapter.findAll()

        assertSame(expectedCollaborators, actualCollaborators)
    }

    @Test
    fun `Finds collaborators by order number`() {
        val expectedCollaborators = listOf(createCollaborator())
        doReturn(expectedCollaborators).`when`(collaboratorMongoRepositorySpy).findByOrderNumberIsGreaterThanEqual(5)

        val actualCollaborators = adapter.findByOrderNumberIsGreaterThanEqual(5)

        assertSame(expectedCollaborators, actualCollaborators)
    }

    companion object {
        private val COLLABORATOR_ID = "17bd6e82c95af430"
        private val COLLABORATOR_NAME = "Default Name"
        private val COLLABORATOR_NUMBER = "+34654324689"

        private fun createCollaborator(workTime: TimeRange = TimeRange(0, 24)) = Collaborator().apply {
            this.id = COLLABORATOR_ID
            this.name = COLLABORATOR_NAME
            this.mobileNumber = COLLABORATOR_NUMBER
            this.address = Address()
            this.workTime = workTime
        }
    }

}