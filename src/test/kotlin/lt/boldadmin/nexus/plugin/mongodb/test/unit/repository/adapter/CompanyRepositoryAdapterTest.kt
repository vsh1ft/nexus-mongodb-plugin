package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyMongoRepository
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CompanyRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CompanyClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class CompanyRepositoryAdapterTest {

    @Mock
    private lateinit var companyMongoRepositorySpy: CompanyMongoRepository

    private lateinit var adapter: CompanyRepositoryAdapter

    @Before
    fun setUp() {
        adapter = CompanyRepositoryAdapter(companyMongoRepositorySpy)
    }

    @Test
    fun `Saves company as clone`() {
        val company = createCompany()
        doAnswer { invocation ->
            val user = invocation.arguments[0] as CompanyClone
            user.apply { id = COMPANY_ID }
        }.`when`(companyMongoRepositorySpy).save<CompanyClone>(any())

        adapter.save(company)

        argumentCaptor<CompanyClone>().apply {
            verify(companyMongoRepositorySpy).save(capture())
            assertEquals(COMPANY_ID, firstValue.id)
            assertEquals(COMPANY_NAME, firstValue.name)
            assertEquals(CUSTOMERS, firstValue.customers)
            assertEquals(COLLABORATORS, firstValue.collaborators)
        }
    }

    @Test
    fun `Exists by company name`() {
        doReturn(true).`when`(companyMongoRepositorySpy).existsByName(COMPANY_NAME)

        val expected = adapter.existsByName(COMPANY_NAME)

        assertTrue(expected)
    }

    @Test
    fun `Does not exist by company name`() {
        doReturn(false).`when`(companyMongoRepositorySpy).existsByName(COMPANY_NAME)

        val expected = adapter.existsByName(COMPANY_NAME)

        assertFalse(expected)
    }

    private fun createCompany() =
        Company().apply {
            id = COMPANY_ID
            name = COMPANY_NAME
            customers = CUSTOMERS
            collaborators = COLLABORATORS
        }

    companion object {
        private val COMPANY_ID = "COMPANY_ID"
        private val COMPANY_NAME = "COMPANY_NAME"
        private val CUSTOMERS = mutableListOf(Customer())
        private val COLLABORATORS = mutableListOf(Collaborator())
    }

}