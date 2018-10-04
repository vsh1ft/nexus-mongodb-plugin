package lt.boldadmin.nexus.plugin.mongodb.test.unit.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.adapter.CountryRepositoryMongodbGatewayAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryRepository
import org.junit.Test
import kotlin.test.assertEquals

class CountryRepositoryMongodbGatewayAdapterTest {


    @Test
    fun `Retrieves all countries`() {
        val countryRepositorySpy: CountryRepository = mock()
        val expectedCountries = listOf(Country("Lithuania"))

        doReturn(expectedCountries).`when`(countryRepositorySpy).findAll()


        assertEquals(expectedCountries, CountryRepositoryMongodbGatewayAdapter(countryRepositorySpy).findAll())
    }

}