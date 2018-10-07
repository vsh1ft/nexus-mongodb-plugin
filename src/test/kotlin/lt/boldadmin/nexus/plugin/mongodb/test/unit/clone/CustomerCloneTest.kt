package lt.boldadmin.nexus.plugin.mongodb.test.unit.clone

import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.clone.CustomerClone
import org.junit.Test
import kotlin.test.assertEquals

class CustomerCloneTest {

    @Test
    fun `Sets clone`() {
        val expectedClone = createCustomerClone()

        val actualClone = CustomerClone().apply { set(createCustomer()) }

        assertEquals(expectedClone.id, actualClone.id)
        assertEquals(expectedClone.name, actualClone.name)
        assertEquals(expectedClone.address, actualClone.address)
        assertEquals(expectedClone.email, actualClone.email)
        assertEquals(expectedClone.projects, actualClone.projects)
        assertEquals(expectedClone.organizationNumber, actualClone.organizationNumber)
        assertEquals(expectedClone.mobileNumber, actualClone.mobileNumber)
        assertEquals(expectedClone.orderNumber, actualClone.orderNumber)
    }

    @Test
    fun `Converts clone to Customer`() {
        val expectedCustomer = createCustomer()

        val actualCustomer = createCustomerClone().convertToCustomer()

        assertEquals(expectedCustomer.id, actualCustomer.id)
        assertEquals(expectedCustomer.name, actualCustomer.name)
        assertEquals(expectedCustomer.address, actualCustomer.address)
        assertEquals(expectedCustomer.email, actualCustomer.email)
        assertEquals(expectedCustomer.projects, actualCustomer.projects)
        assertEquals(expectedCustomer.organizationNumber, actualCustomer.organizationNumber)
        assertEquals(expectedCustomer.mobileNumber, actualCustomer.mobileNumber)
        assertEquals(expectedCustomer.orderNumber, actualCustomer.orderNumber)
    }

    private fun createCustomerClone() = CustomerClone().apply {
        id = CUSTOMER_ID
        name = CUSTOMER_NAME
        address = CUSTOMER_ADDRESS
        email = CUSTOMER_EMAIL
        organizationNumber = ORGANIZATION_NUMBER
        mobileNumber = CUSTOMER_MOBILE_NUMBER
        orderNumber = CUSTOMER_ORDER_NUMBER
        addProject(CUSTOMER_PROJECT)
    }

    private fun createCustomer() = Customer().apply {
        id = CUSTOMER_ID
        name = CUSTOMER_NAME
        address = CUSTOMER_ADDRESS
        email = CUSTOMER_EMAIL
        organizationNumber = ORGANIZATION_NUMBER
        mobileNumber = CUSTOMER_MOBILE_NUMBER
        orderNumber = CUSTOMER_ORDER_NUMBER
        addProject(CUSTOMER_PROJECT)
    }

    companion object {
        private val CUSTOMER_ID = "CUSTOMER_ID"
        private val CUSTOMER_NAME = "CUSTOMER_NAME"
        private val CUSTOMER_ADDRESS = Address()
        private val CUSTOMER_EMAIL = "EMAIL"
        private val CUSTOMER_PROJECT = Project()
        private val ORGANIZATION_NUMBER = "NUMBER"
        private val CUSTOMER_MOBILE_NUMBER = "+123456789"
        private val CUSTOMER_ORDER_NUMBER: Short = 123
    }
}