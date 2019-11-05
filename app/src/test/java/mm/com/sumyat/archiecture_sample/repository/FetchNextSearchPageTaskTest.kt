package mm.com.sumyat.archiecture_sample.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import mm.com.sumyat.archiecture_sample.api.PlayingMoviewsResponse
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.cache.db.MovieDao
import mm.com.sumyat.archiecture_sample.cache.db.SampleDb
import mm.com.sumyat.archiecture_sample.util.TestUtil
import mm.com.sumyat.archiecture_sample.util.mock
import mm.com.sumyat.archiecture_sample.vo.Resource
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response
import java.io.IOException


@RunWith(JUnit4::class)
class FetchNextSearchPageTaskTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: SampleService

    private lateinit var db: SampleDb

    private lateinit var repoDao: MovieDao

    private lateinit var task: FetchNextSearchPageTask

    private val observer: Observer<Resource<Boolean>> = mock()

    @Before
    fun init() {
        service = Mockito.mock(SampleService::class.java)
        db = Mockito.mock(SampleDb::class.java)
        Mockito.`when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repoDao = Mockito.mock(MovieDao::class.java)
        Mockito.`when`(db.movieDao()).thenReturn(repoDao)
        task = FetchNextSearchPageTask(2, service, db)
        task.liveData.observeForever(observer)
    }

    @Test
    fun withoutResult() {
        Mockito.`when`(repoDao.getMovies()).thenReturn(null)
        task.run()
        Mockito.verify(observer).onChanged(null)
        Mockito.verifyNoMoreInteractions(observer)
        Mockito.verifyNoMoreInteractions(service)
    }

    @Test
    fun nextPageWithNull() {
        val repos = TestUtil.createMovies(10, "movie")
        val result = PlayingMoviewsResponse( repos,2)
        val call = createCall(result)
        Mockito.`when`(service.getPlayingMovie(2)).thenReturn(call)
        task.run()

        Mockito.verify(repoDao).insertRepos(TestUtil.movieResultToMovie(repos))
        Mockito.verify(observer).onChanged(null)
    }

    @Test
    fun nextPageWithMore() {
        val repos = TestUtil.createMovies(10, "movie")
        val result = PlayingMoviewsResponse( repos,2)
        val call = createCall(result)
        Mockito.`when`(service.getPlayingMovie(2)).thenReturn(call)
        task.run()
        Mockito.verify(repoDao).insertRepos(TestUtil.movieResultToMovie(repos))
        Mockito.verify(observer).onChanged(Resource.success(true))
    }

    @Test
    fun nextPageApiError() {
        val call = mock<Call<PlayingMoviewsResponse>>()
        Mockito.`when`(call.execute()).thenReturn(
            Response.error(
                400, ResponseBody.create(
                    MediaType.parse("txt"), "bar"
                )
            )
        )
        Mockito.`when`(service.getPlayingMovie(2)).thenReturn(call)
        task.run()
        Mockito.verify(observer)!!.onChanged(Resource.error("bar", true))
    }

    @Test
    fun nextPageIOError() {
        val call = mock<Call<PlayingMoviewsResponse>>()
        Mockito.`when`(call.execute()).thenThrow(IOException("bar"))
        Mockito.`when`(service.getPlayingMovie(2)).thenReturn(call)
        task.run()
        Mockito.verify(observer)!!.onChanged(Resource.error("bar", true))
    }

    private fun createCall(body: PlayingMoviewsResponse): Call<PlayingMoviewsResponse> {
        val headers =
            Headers
                .of(
                    "link",
                    "<https://api.themoviedb.org/3/movie/603/similar?api_key=ec9af24f289764814477679301a30e7c&page=" + 2
                            + ">; rel=\"next\""
                )
        val success = if (headers == null)
            Response.success(body)
        else
            Response.success(body, headers)
        val call = mock<Call<PlayingMoviewsResponse>>()
        Mockito.`when`(call.execute()).thenReturn(success)

        return call
    }
}