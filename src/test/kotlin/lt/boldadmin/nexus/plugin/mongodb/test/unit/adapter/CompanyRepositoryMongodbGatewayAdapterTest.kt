package lt.boldadmin.nexus.plugin.mongodb.test.unit.adapter

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.adapter.CompanyRepositoryMongodbGatewayAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CompanyRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.CompanyClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)

class CompanyRepositoryMongodbGatewayAdapterTest {

    @Mock
    private lateinit var companyRepositorySpy: CompanyRepository

    private lateinit var adapter: CompanyRepositoryMongodbGatewayAdapter

    @Before
    fun setUp() {
        adapter = CompanyRepositoryMongodbGatewayAdapter(companyRepositorySpy)
    }

    @Test
    fun `Saves company as clone`() {
        val company = createCompany()

        adapter.save(company)

        argumentCaptor<CompanyClone>().apply {
            verify(companyRepositorySpy).save(capture())
            assertEquals(COMPANY_ID, firstValue.id)
            assertEquals(COMPANY_NAME, firstValue.name)
            assertEquals(CUSTOMERS, firstValue.customers)
            assertEquals(COLLABORATORS, firstValue.collaborators)
        }
    }

    @Test
    fun `Retrieves real company object by name`() {
        val companyClone = createCompanyClone()
        doReturn(companyClone).`when`(companyRepositorySpy).findByName(COMPANY_NAME)

        val actualCompany = adapter.findByName(COMPANY_NAME)

        assertEquals(companyClone.id, actualCompany!!.id)
        assertEquals(companyClone.name, actualCompany.name)
        assertEquals(companyClone.customers, actualCompany.customers)
        assertEquals(companyClone.collaborators, actualCompany.collaborators)
    }

    private fun createCompany() =
        Company().apply {
            id = COMPANY_ID
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