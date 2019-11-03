package mm.com.sumyat.archiecture_sample.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SampleService {

    @GET("https://api.themoviedb.org/3/movie/now_playing?api_key=ec9af24f289764814477679301a30e7c")
    fun searchPlayingMovies(@Query("page") page: Int): LiveData<ApiResponse<PlayingMoviewsResponse>>
}