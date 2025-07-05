package com.bridge.androidtechnicaltest.network

import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO
import com.bridge.androidtechnicaltest.data.network.PupilApi
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class PupilApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: PupilApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build()

        api = retrofit.create(PupilApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getPupils should return correct data`(): Unit = runTest {
        // Arrange
        val mockJson = """
    {
      "itemCount": 1,
      "items": [
        {
          "pupilId": 1,
          "name": "Chima Bayo",
          "country": "Nigeria",
          "image": "http://image.url",
          "latitude": 5.0,
          "longitude": 5.0
        }
      ],
      "pageNumber": 1,
      "totalPages": 1
    }
""".trimIndent()

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson)
        )


        val response = api.getPupils()

        // Assert
        assertEquals(1, response.items?.size)
        assertEquals("Chima Bayo", response.items?.get(0)?.name)

    }


    @Test
    fun `deletePupilById should return 200 OK`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        api.deletePupilById(1)
    }

    @Test
    fun `update pupil should change existing value`() = runTest {
        val updated = PupilDTO(
            pupilId = 456,
            name = "Updated Name",
            country = "Ghana",
            image = "https://example.com/images.jpg",
            latitude = 5.6037,
            longitude = 0.1870
        )

        val mockJson = """
    {
      "pupilId": 456,
      "name": "Updated Name",
      "country": "Ghana",
      "image": "https://example.com/images.jpg",
      "latitude": 5.6037,
      "longitude": 1.1870
    }
""".trimIndent()


        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson)
        )

        val response = api.updatePupil(456, updated)
        assertEquals("Updated Name", response.name)
        assertEquals("Ghana", response.country)
    }

    @Test
    fun `createPupil should return the created pupil`() = runTest {
        val requestPupil = PupilDTO(
            name = "Jane Ade",
            country = "Kenya",
            image = "https://example.com/images.jpg",
            latitude = 5.6037,
            longitude = 0.1870,
            pupilId = 7665
        )

        val mockJson = """
        {
          "pupilId": 2,
          "name": "Jane Ade",
          "country": "Kenya",
          "image" = "https://example.com/images.jpg",
          "latitude" = "5.6037",
          "longitude" = "0.1870"
        }
    """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setResponseCode(201).setBody(mockJson)
        )

        val response = api.createPupil(requestPupil)
        assertEquals("Jane Ade", response.name)
        assertEquals("Kenya", response.country)
    }

    @Test
    fun `getPupilsById should return the correct pupil`() = runTest {
        val mockJson = """
        {
          "pupilId": 675,
          "name": "Chinedu Okafor",
          "country": "Nigeria",
          "image": "https://example.com/images/chinedu_okafor.jpg",
          "latitude": 6.5244,
          "longitude": 3.3792
        }
    """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJson)
        )

        val response = api.getPupilById(675)

        assertEquals("Chinedu Okafor", response.name)
        assertEquals(675L, response.pupilId)
        assertEquals("Nigeria", response.country)
        assertEquals("https://example.com/images/chinedu_okafor.jpg", response.image)

    }


}
