package lt.boldadmin.nexus.plugin.mongodb.test.unit.repository.adapter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import lt.boldadmin.nexus.api.type.valueobject.Country
import lt.boldadmin.nexus.plugin.mongodb.repository.adapter.CountryRepositoryAdapter
import lt.boldadmin.nexus.plugin.mongodb.repository.CountryMongodbRepository
import org.junit.Test
import kotlin.test.assertEquals

class CountryMongodbRepositoryAdapterTest {


    @Test
    fun `Gets all countries`() {
        val countryMongodbRepositorySpy: CountryMongodbRepository = mock()
        val expectedCountries = listOf(Country("Lithuania"))
        doReturn(expectedCountries).`when`(countryMongodbRepositorySpy).findAll()

        val actualCountries = CountryRepositoryAdapter(
            countryMongodbRepositorySpy
        ).findAll()

        assertEquals(expectedCountries, actualCountries)
    }

}