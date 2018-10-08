package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.ProjectRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.ProjectMongodbRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class ProjectMongodbRepositoryAdapterTest {

    @Mock
    private lateinit var projectMongodbRepositorySpy: ProjectMongodbRepository

    private lateinit var adapter: ProjectRepositoryAdapter

    @Before
    fun setUp() {
        adapter = ProjectRepositoryAdapter(projectMongodbRepositorySpy)
    }

    @Test
    fun `Saves project`() {
        val project = createProject()

        adapter.save(project)

        verify(projectMongodbRepositorySpy).save(project)
    }

    @Test
    fun `Finds project by id`() {
        val expectedProject = Optional.of(createProject())
        doReturn(expectedProject).`when`(projectMongodbRepositorySpy).findById(PROJECT_ID)

        val actualProject = adapter.findById(PROJECT_ID)

        assertEquals(expectedProject.get(), actualProject)
    }

    @Test
    fun `Finds by order number`() {
        val orderNumber: Short = 5
        val expectedProjects = listOf(createProject())
        doReturn(expectedProjects).`when`(projectMongodbRepositorySpy).findByOrderNumberIsGreaterThanEqual(orderNumber)

        val actualProjects = adapter.findByOrderNumberIsGreaterThanEqual(orderNumber)

        assertEquals(expectedProjects, actualProjects)
    }

    companion object {
        private val PROJECT_ID = "PROJECT_ID"
        private val PROJECT_NAME = "PROJECT_NAME"

        private fun createProject() = Project().apply {
            id = PROJECT_ID
            name = PROJECT_NAME
            address = Address()
        }
    }

}