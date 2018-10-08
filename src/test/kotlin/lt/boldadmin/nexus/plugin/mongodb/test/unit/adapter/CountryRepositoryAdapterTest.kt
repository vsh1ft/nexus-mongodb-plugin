package lt.boldadmin.nexus.plugin.mongodb.test.unit.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.adapter.CountryRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryRepository
import org.junit.Test
import kotlin.test.assertEquals

class CountryRepositoryAdapterTest {


    @Test
    fun `Gets all countries`() {
        val countryRepositorySpy: CountryRepository = mock()
        val expectedCountries = listOf(Country("Lithuania"))
        doReturn(expectedCountries).`when`(countryRepositorySpy).findAll()

        val actualCountries = CountryRepositoryAdapter(countryRepositorySpy).findAll()

        assertEquals(expectedCountries, actualCountries)
    }

}