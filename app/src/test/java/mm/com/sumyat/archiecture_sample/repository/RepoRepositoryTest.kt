package mm.com.sumyat.archiecture_sample.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import mm.com.sumyat.archiecture_sample.api.PlayingMoviewsResponse
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.cache.PreferencesHelper
import mm.com.sumyat.archiecture_sample.cache.db.MovieDao
import mm.com.sumyat.archiecture_sample.cache.db.SampleDb
import mm.com.sumyat.archiecture_sample.util.ApiUtil.successCall
import mm.com.sumyat.archiecture_sample.util.InstantAppExecutors
import mm.com.sumyat.archiecture_sample.util.TestUtil
import mm.com.sumyat.archiecture_sample.util.mock
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(JUnit4::class)
class RepoRepositoryTest {

    private lateinit var repository: RepoRepository
    private val dao = Mockito.mock(MovieDao::class.java)
    private val service = Mockito.mock(SampleService::class.java)
    private val prefHelper = Mockito.mock(PreferencesHelper::class.java)
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val db = Mockito.mock(SampleDb::class.java)
        Mockito.`when`(db.movieDao()).thenReturn(dao)
        Mockito.`when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = RepoRepository(InstantAppExecutors(), db, dao, service, prefHelper)
    }

    @Test
    fun searchMoviesFromNetwork() {
        val dbData = MutableLiveData<List<Movie>>()
        Mockito.`when`(dao.getMovies()).thenReturn(dbData)

        val repo1 = TestUtil.createMovieResponse("movie1")
        val repo2 = TestUtil.createMovieResponse("movie2")

        val repoList = arrayListOf(repo1, repo2)
        val apiResponse = PlayingMoviewsResponse(repoList, 1)

        val call = successCall(apiResponse)
        Mockito.`when`(service.getPlayingMovie()).thenReturn(call)

        val data = repository.searchMovie()
        Mockito.verify(dao).getMovies()
        Mockito.verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<List<Movie>>>>()
        data.observeForever(observer)
        Mockito.verifyNoMoreInteractions(service)
        Mockito.verify(observer).onChanged(Resource.loading(null))
        val updatedDbData = MutableLiveData<List<Movie>>()
        Mockito.`when`(dao.getMovies()).thenReturn(updatedDbData)

        val movies = TestUtil.movieResultToMovie(repoList)

        dbData.postValue(null)
        Mockito.verify(service).getPlayingMovie()
        Mockito.verify(dao).insertRepos(movies)

        updatedDbData.postValue(movies)
        Mockito.verify(observer).onChanged(Resource.success(movies))
    }

}