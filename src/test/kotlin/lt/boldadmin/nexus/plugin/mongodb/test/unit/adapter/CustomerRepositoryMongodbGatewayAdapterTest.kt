package lt.boldadmin.nexus.plugin.mongodb.test.unit.adapter

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.adapter.CustomerRepositoryMongodbGatewayAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerRepository
import lt.boldadmin.nexus.plugin.mongodb.type.entity.CustomerClone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CustomerRepositoryMongodbGatewayAdapterTest {

    @Mock
    private lateinit var customerRepositorySpy: CustomerRepository

    private lateinit var adapter: CustomerRepositoryMongodbGatewayAdapter

    @Before
    fun setUp() {
        adapter = CustomerRepositoryMongodbGatewayAdapter(customerRepositorySpy)
    }


    @Test
    fun `Saves customer as clone`() {
        val customer = createCustomer()

        adapter.save(customer)

        argumentCaptor<CustomerClone>().apply {
            verify(customerRepositorySpy).save(capture())
            assertEquals(CUSTOMER_ID, firstValue.id)
            assertEquals(CUSTOMER_NAME, firstValue.name)
            assertEquals(CUSTOMER_ADDRESS, firstValue.address)
            assertEquals(CUSTOMER_EMAIL, firstValue.email)
            assertEquals(CUSTOMER_PROJECT, firstValue.projects.first())
            assertEquals(ORGANIZATION_NUMBER, firstValue.organizationNumber)
            assertEquals(CUSTOMER_MOBILE_NUMBER, firstValue.mobileNumber)
            assertEquals(CUSTOMER_ORDER_NUMBER, firstValue.orderNumber)
        }
    }

    @Test
    fun `Retrieves real customer object by id`() {
        val customerClone = createCustomerClone()
        doReturn(Optional.of(customerClone)).`when`(customerRepositorySpy).findById(CUSTOMER_ID)

        val actualCustomer = adapter.findById(CUSTOMER_ID)

        assertEquals(CUSTOMER_ID, actualCustomer.id)
        assertEquals(CUSTOMER_NAME, actualCustomer.name)
        assertEquals(CUSTOMER_ADDRESS, actualCustomer.address)
        assertEquals(CUSTOMER_EMAIL, actualCustomer.email)
        assertEquals(CUSTOMER_PROJECT, actualCustomer.projects.first())
        assertEquals(ORGANIZATION_NUMBER, actualCustomer.organizationNumber)
        assertEquals(CUSTOMER_MOBILE_NUMBER, actualCustomer.mobileNumber)
        assertEquals(CUSTOMER_ORDER_NUMBER, actualCustomer.orderNumber)
    }

    @Test
    fun `Retrieves real customer object by order number`() {
        val orderNumber: Short = 8
        val customerClone = createCustomerClone()
        doReturn(listOf(customerClone)).`when`(customerRepositorySpy).findByOrderNumberIsGreaterThanEqual(orderNumber)

        val actualCustomers = adapter.findByOrderNumberIsGreaterThanEqual(orderNumber)

        assertEquals(CUSTOMER_ID, actualCustomers.first().id)
        assertEquals(CUSTOMER_NAME, actualCustomers.first().name)
        assertEquals(CUSTOMER_ADDRESS, actualCustomers.first().address)
        assertEquals(CUSTOMER_EMAIL, actualCustomers.first().email)
        assertEquals(CUSTOMER_PROJECT, actualCustomers.first().projects.first())
        assertEquals(ORGANIZATION_NUMBER, actualCustomers.first().organizationNumber)
        assertEquals(CUSTOMER_MOBILE_NUMBER, actualCustomers.first().mobileNumber)
        assertEquals(CUSTOMER_ORDER_NUMBER, actualCustomers.first().orderNumber)
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