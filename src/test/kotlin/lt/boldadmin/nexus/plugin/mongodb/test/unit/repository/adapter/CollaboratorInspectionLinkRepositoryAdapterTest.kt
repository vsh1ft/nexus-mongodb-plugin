package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.CollaboratorInspectionLink
import lt.boldadmin.nexus.plugin.mongodb.repository.CollaboratorInspectionLinkMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CollaboratorInspectionLinkRepositoryAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertSame
import kotlin.test.assertTrue


@RunWith(MockitoJUnitRunner::class)
class CollaboratorInspectionLinkRepositoryAdapterTest {

    @Mock
    private lateinit var repositorySpy: CollaboratorInspectionLinkMongoRepository

    private lateinit var adapter: CollaboratorInspectionLinkRepositoryAdapter

    @Before
    fun setUp() {
        adapter = CollaboratorInspectionLinkRepositoryAdapter(repositorySpy)
    }

    @Test
    fun `Saves inspection link`() {
        val inspectionLink = createInspectionLink()

        adapter.save(inspectionLink)

        verify(repositorySpy).save(inspectionLink)
    }

    @Test
    fun `Gets inspectionLink by id`() {
        val expectedInspectionLink = createInspectionLink()
        doReturn(Optional.of(expectedInspectionLink)).`when`(repositorySpy).findById(ID)

        val actualInspectionLink = adapter.findById(ID)

        assertSame(expectedInspectionLink, actualInspectionLink)
    }

    @Test
    fun `Gets inspectionLink by link`() {
        val expectedInspectionLink = createInspectionLink()
        doReturn(expectedInspectionLink).`when`(repositorySpy).findByLink(LINK)

        val actualInspectionLink = adapter.findByLink(LINK)

        assertSame(expectedInspectionLink, actualInspectionLink)
    }

    @Test
    fun `Confirms that inspection link exists by link`() {
        doReturn(true).`when`(repositorySpy).existsByLink(LINK)

        val exists = adapter.existsByLink(LINK)

        assertTrue(exists)
    }

    @Test
    fun `Confirms that inspection link exists by id`() {
        doReturn(true).`when`(repositorySpy).existsById(ID)

        val exists = adapter.existsById(ID)

        assertTrue(exists)
    }

    @Test
    fun `Deletes inspection link by id`() {
       adapter.deleteById(ID)

        verify(repositorySpy).deleteById(ID)
    }


    companion object {
        private val ID = "17bd6e82c95af430"
        private val LINK = "some_link"

        private fun createInspectionLink() = CollaboratorInspectionLink(ID, LINK)
    }

}