package mm.com.sumyat.archiecture_sample.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import mm.com.sumyat.archiecture_sample.util.LiveDataCallAdapterFactory
import mm.com.sumyat.archiecture_sample.util.LiveDataTestUtil.getValue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsNull
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class SampleServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: SampleService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(SampleService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getPlayingMovies(){
        enqueueResponse("movies.json")
        val repos = (getValue(service.getPlayingMovie()) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/now_playing?page=1"))

        val results = repos.results

        Assert.assertThat(results.size, CoreMatchers.`is`(20))

        val movie = results[0]
        Assert.assertThat(movie, IsNull.notNullValue())
        Assert.assertThat(movie.title, CoreMatchers.`is`("Joker"))
        Assert.assertThat(movie.release_date, CoreMatchers.`is`("2019-10-04"))

        val page = repos.page
        Assert.assertThat(page, CoreMatchers.`is`(1))
    }

    @Test
    fun getSimilarMovies(){
        enqueueResponse("similar.json")
        val repos = (getValue(service.getSimilarMovies(603)) as ApiSuccessResponse).body.results

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/603/similar?page=1"))

        Assert.assertThat(repos.size, CoreMatchers.`is`(15))

        val repo = repos[0]
        Assert.assertThat(repo.title, CoreMatchers.`is`("Blade Runner 2049"))
        Assert.assertThat(repo.popularity, CoreMatchers.`is`(43.929))
    }


    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}