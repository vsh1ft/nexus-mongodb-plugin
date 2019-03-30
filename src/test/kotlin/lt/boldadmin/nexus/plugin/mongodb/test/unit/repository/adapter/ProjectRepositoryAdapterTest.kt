package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.ProjectRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectMongoRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class ProjectRepositoryAdapterTest {

    @Mock
    private lateinit var projectMongoRepositorySpy: ProjectMongoRepository

    private lateinit var adapter: ProjectRepositoryAdapter

    @Before
    fun setUp() {
        adapter = ProjectRepositoryAdapter(projectMongoRepositorySpy)
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

    companion object {
        private val PROJECT_ID = "PROJECT_ID"
        private val PROJECT_NAME = "PROJECT_NAME"

        private fun createProject() = Project().apply {
            id = PROJECT_ID
            name = PROJECT_NAME
            location = Location(1.1, 1.1)
        }
    }

}