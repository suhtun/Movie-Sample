package mm.com.sumyat.archiecture_sample.repository

import androidx.lifecycle.LiveData
import com.android.example.github.repository.NetworkBoundResource
import mm.com.sumyat.archiecture_sample.AppExecutors
import mm.com.sumyat.archiecture_sample.api.ApiSuccessResponse
import mm.com.sumyat.archiecture_sample.api.PlayingMoviewsResponse
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.cache.PreferencesHelper
import mm.com.sumyat.archiecture_sample.cache.db.MovieDao
import mm.com.sumyat.archiecture_sample.cache.db.SampleDb
import mm.com.sumyat.archiecture_sample.testing.OpenForTesting
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: SampleDb,
    private val movieDao: MovieDao,
    private val service: SampleService,
    private val preferencesHelper: PreferencesHelper
) {

    fun searchNextPage(): LiveData<Resource<Boolean>> {
        setPage()
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            page = getPage(),
            service = service,
            db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun getPage(): Int {
        return preferencesHelper.nextPage
    }

    private fun setPage() {
        preferencesHelper.nextPage = getPage() + 1
    }

    fun searchMovie(): LiveData<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, PlayingMoviewsResponse>(appExecutors) {

            override fun saveCallResult(items: PlayingMoviewsResponse) {
                db.runInTransaction {
                    movieDao.insertMovies(items.movieResults.map {
                        Movie(
                            it.id,
                            it.title,
                            it.poster_path,
                            it.vote_average.toString(),
                            it.overview,
                            it.release_date
                        )
                    })
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb() = movieDao.getMovies()

            override fun createCall() = service.getPlayingMovie()

            override fun processResponse(response: ApiSuccessResponse<PlayingMoviewsResponse>)
                    : PlayingMoviewsResponse {
                val body = response.body
                return body
            }
        }.asLiveData()
    }
}