package mm.com.sumyat.archiecture_sample.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.android.example.github.repository.DetailNetworkBoundResource
import mm.com.sumyat.archiecture_sample.AppExecutors
import mm.com.sumyat.archiecture_sample.api.ApiSuccessResponse
import mm.com.sumyat.archiecture_sample.api.PlayingMoviewsResponse
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.cache.db.MovieDao
import mm.com.sumyat.archiecture_sample.cache.db.SampleDb
import mm.com.sumyat.archiecture_sample.util.AbsentLiveData
import mm.com.sumyat.archiecture_sample.vo.MDetail
import mm.com.sumyat.archiecture_sample.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton
import mm.com.sumyat.archiecture_sample.vo.Next

@Singleton
class DetailRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: SampleDb,
    private val movieDao: MovieDao,
    private val service: SampleService
) {

    fun searchNextPage(movieid: Int): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchDeatilPageTask(
            movie_id = movieid,
            service = service,
            db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(movieid: Int): LiveData<Resource<List<MDetail>>> {
        return object : DetailNetworkBoundResource<List<MDetail>, PlayingMoviewsResponse>(appExecutors) {

            override fun saveCallResult(items: PlayingMoviewsResponse) {
                db.runInTransaction {
                    movieDao.insertNext(Next(movieid, 1))
                    movieDao.insertDetails(items.movieResults.map {
                        MDetail(
                            it.id,
                            it.title,
                            it.poster_path,
                            it.release_date,
                            movieid
                        )
                    })
                }
            }

            override fun shouldFetch(data: List<MDetail>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<MDetail>> {
                return Transformations.switchMap(movieDao.searchMDetails(movieid)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        movieDao.searchMDetails(movieid)
                    }
                }
            }

            override fun createCall() = service.getSimilarMovies(movieid)

            override fun processResponse(response: ApiSuccessResponse<PlayingMoviewsResponse>)
                    : PlayingMoviewsResponse {
                val body = response.body
                return body
            }
        }.asLiveData()
    }
}