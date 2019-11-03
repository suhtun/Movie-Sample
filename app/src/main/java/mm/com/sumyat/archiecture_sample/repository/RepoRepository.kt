package mm.com.sumyat.archiecture_sample.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import mm.com.sumyat.archiecture_sample.AppExecutors
import mm.com.sumyat.archiecture_sample.api.ApiSuccessResponse
import mm.com.sumyat.archiecture_sample.api.PlayingMoviewsResponse
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.db.MovieDao
import mm.com.sumyat.archiecture_sample.db.SampleDb
import mm.com.sumyat.archiecture_sample.util.AbsentLiveData
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(private val appExecutors: AppExecutors,
                                         private val db: SampleDb,
                                         private val movieDao: MovieDao,
                                         private val service: SampleService){

    fun search(page: Int): LiveData<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, PlayingMoviewsResponse>(appExecutors) {

            override fun saveCallResult(items: PlayingMoviewsResponse) {
                db.runInTransaction {
                    movieDao.insertRepos(items.results.map { Movie(it.id,it.title,it.poster_path,it.overview,it.release_date) })
                }
            }

            override fun shouldFetch(data: List<Movie>?) : Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return Transformations.switchMap(movieDao.search()) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        movieDao.search()
                    }
                }
            }

            override fun createCall() = service.searchPlayingMovies(page)

            override fun processResponse(response: ApiSuccessResponse<PlayingMoviewsResponse>)
                    : PlayingMoviewsResponse {
                val body = response.body
                return body
            }
        }.asLiveData()
    }
}