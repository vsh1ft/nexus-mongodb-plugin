package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.type.entity.Customer
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.Address
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CustomerRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.type.entity.clone.CustomerClone
import lt.boldadmin.nexus.plugin.mongodb.repository.CustomerMongoRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CustomerRepositoryAdapterTest {

    @Mock
    private lateinit var customerMongoRepositorySpy: CustomerMongoRepository

    private lateinit var adapter: CustomerRepositoryAdapter

    @Before
    fun setUp() {
        adapter = CustomerRepositoryAdapter(customerMongoRepositorySpy)
    }

    @Test
    fun `Saves customer as clone`() {
        val customer = createCustomer()
        doAnswer { invocation ->
            val user = invocation.arguments[0] as CustomerClone
            user.apply { id = CUSTOMER_ID }
        }.`when`(customerMongoRepositorySpy).save<CustomerClone>(any())


        adapter.save(customer)

        argumentCaptor<CustomerClone>().apply {
            verify(customerMongoRepositorySpy).save(capture())
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
    fun `Gets customer by id`() {
        val customerClone = createCustomerClone()
        doReturn(Optional.of(customerClone)).`when`(customerMongoRepositorySpy).findById(CUSTOMER_ID)

        val actualCustomer = adapter.findById(CUSTOMER_ID)

        assertCustomerFieldsAreEqual(actualCustomer)
    }

    @Test
    fun `Gets customers by order number`() {
        val orderNumber: Short = 8
        val customerClone = createCustomerClone()
        doReturn(listOf(customerClone)).`when`(customerMongoRepositorySpy).findByOrderNumberIsGreaterThanEqual(orderNumber)

        val actualCustomers = adapter.findByOrderNumberIsGreaterThanEqual(orderNumber)

        assertCustomerFieldsAreEqual(actualCustomers.single())
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
        id = null
        name = CUSTOMER_NAME
        address = CUSTOMER_ADDRESS
        email = CUSTOMER_EMAIL
        organizationNumber = ORGANIZATION_NUMBER
        mobileNumber = CUSTOMER_MOBILE_NUMBER
        orderNumber = CUSTOMER_ORDER_NUMBER
        addProject(CUSTOMER_PROJECT)
    }

    private fun assertCustomerFieldsAreEqual(actualCustomer: Customer) {
        assertEquals(CUSTOMER_ID, actualCustomer.id)
        assertEquals(CUSTOMER_NAME, actualCustomer.name)
        assertEquals(CUSTOMER_ADDRESS, actualCustomer.address)
        assertEquals(CUSTOMER_EMAIL, actualCustomer.email)
        assertEquals(CUSTOMER_PROJECT, actualCustomer.projects.first())
        assertEquals(ORGANIZATION_NUMBER, actualCustomer.organizationNumber)
        assertEquals(CUSTOMER_MOBILE_NUMBER, actualCustomer.mobileNumber)
        assertEquals(CUSTOMER_ORDER_NUMBER, actualCustomer.orderNumber)
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