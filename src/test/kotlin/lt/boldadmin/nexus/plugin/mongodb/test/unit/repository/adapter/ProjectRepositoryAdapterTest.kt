package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockitokotlin2.*
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.ProjectRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.addCriteria
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProjectRepositoryAdapterTest {

    @Mock
    private lateinit var projectMongoRepositorySpy: ProjectMongoRepository

    @Mock
    private lateinit var mongoTemplateStub: MongoTemplate

    private lateinit var adapter: ProjectRepositoryAdapter

    @BeforeEach
    fun setUp() {
        adapter = ProjectRepositoryAdapter(projectMongoRepositorySpy, mongoTemplateStub)
    }

    @Test
    fun `Saves project`() {
        val project = createProject()

        adapter.save(project)

        verify(projectMongoRepositorySpy).save(project)
    }

    @Test
    fun `Gets project by id`() {
        val expectedProject = Optional.of(createProject())
        doReturn(expectedProject).`when`(projectMongoRepositorySpy).findById(PROJECT_ID)

        val actualProject = adapter.findById(PROJECT_ID)

        assertEquals(expectedProject.get(), actualProject)
    }

    @Test
    fun `Gets projects by order number`() {
        val orderNumber: Short = 5
        val expectedProjects = listOf(createProject())
        doReturn(expectedProjects).`when`(projectMongoRepositorySpy).findByOrderNumberIsGreaterThanEqual(orderNumber)

        val actualProjects = adapter.findByOrderNumberIsGreaterThanEqual(orderNumber)

        assertEquals(expectedProjects, actualProjects)
    }

    @Test
    fun `Finds Projects by Collaborator Id`() {
        val collaboratorId = "collaboratorId"
        val expectedProjects = mutableListOf(Project(PROJECT_ID))
        val query = Query().addCriteria(
            mapOf("collaborators.\$ref" to "collaborator", "collaborators.\$id" to collaboratorId)
        )
        doReturn(User(projects = expectedProjects)).`when`(mongoTemplateStub).findOne(query, User::class.java)

        val actualProjects = adapter.findByCollaboratorId(collaboratorId)

        assertEquals(expectedProjects, actualProjects)
    }

    @Test
    fun `Provides empty list when cannot find Projects by Collaborator id`() {
        val collaboratorId = "collaboratorId"

        doReturn(null).`when`(mongoTemplateStub).findOne(any(), eq(User::class.java))

        val actualProjects = adapter.findByCollaboratorId(collaboratorId)

        assertEquals(emptyList<Project>(), actualProjects)
    }

    companion object {
        private val PROJECT_ID = "PROJECT_ID"
        private val PROJECT_NAME = "PROJECT_NAME"

        private fun createProject() = Project().apply {
            id = PROJECT_ID
            name = PROJECT_NAME
            location = Location(Coordinates(1.1, 1.1))
        }
    }

}
