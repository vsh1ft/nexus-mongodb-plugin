package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.StartedProjectWorkToken
import lt.boldadmin.nexus.plugin.mongodb.repository.StartedProjectWorkTokenMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.StartedProjectWorkTokenRepositoryAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertSame
import kotlin.test.assertTrue


@RunWith(MockitoJUnitRunner::class)
class StartedProjectWorkTokenRepositoryAdapterTest {

    @Mock
    private lateinit var repositorySpy: StartedProjectWorkTokenMongoRepository

    private lateinit var adapter: StartedProjectWorkTokenRepositoryAdapter

    @Before
    fun `Set up`() {
        adapter = StartedProjectWorkTokenRepositoryAdapter(repositorySpy)
    }

    @Test
    fun `Saves startedWorkToken`() {
        val startedWorkToken = createStartedWorkToken()

        adapter.save(startedWorkToken)

        verify(repositorySpy).save(startedWorkToken)
    }

    @Test
    fun `Gets startedWorkToken by id`() {
        val expectedStartedWorkToken = createStartedWorkToken()
        doReturn(Optional.of(expectedStartedWorkToken)).`when`(repositorySpy).findById(ID)

        val actualStartedWorkToken = adapter.findById(ID)

        assertSame(expectedStartedWorkToken, actualStartedWorkToken)
    }

    @Test
    fun `Gets startedWorkToken by token`() {
        val expectedStartedWorkToken = createStartedWorkToken()
        doReturn(expectedStartedWorkToken).`when`(repositorySpy).findByToken(TOKEN)

        val actualStartedWorkToken = adapter.findByToken(TOKEN)

        assertSame(expectedStartedWorkToken, actualStartedWorkToken)
    }

    @Test
    fun `Confirms that startedWorkToken exists by token`() {
        doReturn(true).`when`(repositorySpy).existsByToken(TOKEN)

        val exists = adapter.existsByToken(TOKEN)

        assertTrue(exists)
    }

    @Test
    fun `Confirms that startedWorkToken exists by id`() {
        doReturn(true).`when`(repositorySpy).existsById(ID)

        val exists = adapter.existsById(ID)

        assertTrue(exists)
    }

    @Test
    fun `Deletes startedWorkToken by id`() {
        adapter.deleteById(ID)

        verify(repositorySpy).deleteById(ID)
    }

    companion object {
        private val ID = "17bd6e82c95af430"
        private val TOKEN = "token"

        private fun createStartedWorkToken() = StartedProjectWorkToken(ID, TOKEN)
    }

}