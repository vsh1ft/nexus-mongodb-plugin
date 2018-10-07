package lt.boldadmin.nexus.plugin.mongodb.test.unit.clone

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Company
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.plugin.mongodb.clone.CompanyClone
import org.junit.Test
import kotlin.test.assertEquals

class CompanyCloneTest{

    @Test
    fun `Sets clone`() {
        val expectedClone = createCompanyClone()

        val actualClone = CompanyClone().apply { set(createCompany()) }

        assertEquals(expectedClone.id, actualClone.id)
        assertEquals(expectedClone.name, actualClone.name)
        assertEquals(expectedClone.customers, actualClone.customers)
        assertEquals(expectedClone.collaborators, actualClone.collaborators)
    }

    @Test
    fun `Converts clone to Company`() {
        val expectedCompany = createCompany()

        val actualCompany = createCompanyClone().convertToCompany()

        assertEquals(expectedCompany.id, actualCompany.id)
        assertEquals(expectedCompany.name, actualCompany.name)
        assertEquals(expectedCompany.customers, actualCompany.customers)
        assertEquals(expectedCompany.collaborators, actualCompany.collaborators)
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