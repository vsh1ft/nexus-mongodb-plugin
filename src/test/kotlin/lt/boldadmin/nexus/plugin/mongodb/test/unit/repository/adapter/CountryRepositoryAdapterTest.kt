package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CountryRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryMongoRepository
import org.junit.Test
import kotlin.test.assertEquals

class CountryRepositoryAdapterTest {


    @Test
    fun `Gets all countries`() {
        val countryMongoRepositorySpy: CountryMongoRepository = mock()
        val expectedCountries = listOf(Country("Lithuania"))
        doReturn(expectedCountries).`when`(countryMongoRepositorySpy).findAll()

        val actualCountries = CountryRepositoryAdapter(
            countryMongoRepositorySpy
        ).findAll()

        assertEquals(expectedCountries, actualCountries)
    }

}