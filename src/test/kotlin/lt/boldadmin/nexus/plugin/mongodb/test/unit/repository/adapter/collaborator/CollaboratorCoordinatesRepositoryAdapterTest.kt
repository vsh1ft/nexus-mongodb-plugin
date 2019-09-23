package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter.collaborator

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import lt.boldadmin.nexus.api.repository.CollaboratorCoordinatesRepository
import lt.boldadmin.nexus.api.type.entity.collaborator.CollaboratorCoordinates
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.collaborator.CollaboratorCoordinatesRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.collaborator.CollaboratorCoordinatesMongoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.remove

@ExtendWith(MockKExtension::class)
class CollaboratorCoordinatesRepositoryAdapterTest {

    @MockK
    private lateinit var mongoRepositorySpy: CollaboratorCoordinatesMongoRepository

    @MockK
    private lateinit var mongoTemplateSpy: MongoTemplate

    private lateinit var adapter: CollaboratorCoordinatesRepository

    @BeforeEach
    fun setUp() {
        adapter = CollaboratorCoordinatesRepositoryAdapter(
            mongoRepositorySpy,
            mongoTemplateSpy
        )
    }

    @Test
    fun `Saves collaborator coordinates`() {
        val collaboratorCoordinates = CollaboratorCoordinates(
            "collabId",
            Coordinates(1.0, 2.0),
            123
        )
        every { mongoRepositorySpy.save(collaboratorCoordinates) } returns Unit

        adapter.save(collaboratorCoordinates)

        verify { mongoRepositorySpy.save(collaboratorCoordinates) }
    }

    @Test
    fun `Removes older collaborator coordinates`() {
        val query = query(where("timestamp").lte(123.toLong()).and("collaboratorId").`is`("a"))
        every { mongoTemplateSpy.remove(query, CollaboratorCoordinates::class) } returns mockk()

        adapter.removeOlderThan("a", 123)

        verify { mongoTemplateSpy.remove(query, CollaboratorCoordinates::class) }
    }

    @Test
    fun `Finds coordinates by collaborator id`() {
        val actualCoordinates = listOf(CollaboratorCoordinates("collabId", Coordinates(1.2, 3.4), 123))
        every { mongoRepositorySpy.findByCollaboratorIdOrderByTimestampDesc("collabId") } returns actualCoordinates

        val expectedCoordinates = adapter.findByCollaboratorId("collabId")

        assertEquals(expectedCoordinates, actualCoordinates)
    }
}
