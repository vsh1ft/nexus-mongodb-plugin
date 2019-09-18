package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import lt.boldadmin.nexus.api.repository.CollaboratorCoordinatesRepository
import lt.boldadmin.nexus.api.type.entity.CollaboratorCoordinates
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorCoordinatesMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CollaboratorCoordinatesRepositoryAdapter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CollaboratorCoordinatesRepositoryAdapterTest {

    @MockK
    private lateinit var mongoRepositorySpy: CollaboratorCoordinatesMongoRepository

    private lateinit var adapter: CollaboratorCoordinatesRepository

    @BeforeEach
    fun setUp() {
        adapter = CollaboratorCoordinatesRepositoryAdapter(mongoRepositorySpy)
    }

    @Test
    fun `Saves collaborator coordinates`() {
        val collaboratorCoordinates = CollaboratorCoordinates("collabId", Coordinates(1.0, 2.0))
        every { mongoRepositorySpy.save(collaboratorCoordinates) } returns Unit

        adapter.save(collaboratorCoordinates)

        verify { mongoRepositorySpy.save(collaboratorCoordinates) }
    }

    @Test
    fun `Removes by collaborator id`() {
        every { mongoRepositorySpy.removeByCollaboratorId("collabId") } returns Unit

        adapter.removeByCollaboratorId("collabId")

        verify { mongoRepositorySpy.removeByCollaboratorId("collabId") }
    }

    @Test
    fun `Confirms that collaborator exists by id`() {
        every { mongoRepositorySpy.existsByCollaboratorId("collabId") } returns true

        val exists = adapter.existsByCollaboratorId("collabId")

        assertTrue(exists)
    }
}
