package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CompanyRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyMongodbRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CompanyClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)

class CompanyMongodbRepositoryAdapterTest {

    @Mock
    private lateinit var companyMongodbRepositorySpy: CompanyMongodbRepository

    private lateinit var adapter: CompanyRepositoryAdapter

    @Before
    fun setUp() {
        adapter = CompanyRepositoryAdapter(companyMongodbRepositorySpy)
    }

    @Test
    fun `Saves company as clone`() {
        val company = createCompany()
        doAnswer { invocation ->
            val user = invocation.arguments[0] as CompanyClone
            user.apply { id = COMPANY_ID }
        }.`when`(companyMongodbRepositorySpy).save<CompanyClone>(any())

        adapter.save(company)

        argumentCaptor<CompanyClone>().apply {
            verify(companyMongodbRepositorySpy).save(capture())
            assertEquals(COMPANY_ID, firstValue.id)
            assertEquals(COMPANY_NAME, firstValue.name)
            assertEquals(CUSTOMERS, firstValue.customers)
            assertEquals(COLLABORATORS, firstValue.collaborators)
        }
    }

    @Test
    fun `Gets company by name`() {
        val companyClone = createCompanyClone()
        doReturn(companyClone).`when`(companyMongodbRepositorySpy).findByName(COMPANY_NAME)

        val actualCompany = adapter.findByName(COMPANY_NAME)

        assertEquals(companyClone.id, actualCompany!!.id)
        assertEquals(companyClone.name, actualCompany.name)
        assertEquals(companyClone.customers, actualCompany.customers)
        assertEquals(companyClone.collaborators, actualCompany.collaborators)
    }

    @Test
    fun `Gets null if company doesn't exist by name`() {
        doReturn(null).`when`(companyMongodbRepositorySpy).findByName(COMPANY_NAME)

        val actualCompany = adapter.findByName(COMPANY_NAME)

        assertNull(actualCompany)
    }

    private fun createCompany() =
        Company().apply {
            id = null
            name = COMPANY_NAME
            customers = CUSTOMERS
            collaborators = COLLABORATORS
        }

    private fun createCompanyClone() =
        CompanyClone().apply {
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