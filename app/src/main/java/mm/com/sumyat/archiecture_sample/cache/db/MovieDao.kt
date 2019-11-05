package mm.com.sumyat.archiecture_sample.cache.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mm.com.sumyat.archiecture_sample.testing.OpenForTesting
import mm.com.sumyat.archiecture_sample.vo.MDetail
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Next

@Dao
@OpenForTesting
abstract class MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepos(repositories: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDetails(repositories: List<MDetail>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertNext(next: Next)

    @Query("UPDATE Next SET next = :next WHERE movie_id = :mid")
    abstract fun updateNext(mid: Int, next: Int): Int

    @Query("SELECT * FROM movie")
    abstract fun getMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM MDetail WHERE `movie_id` = :movie_id")
    abstract fun searchMDetails(movie_id: Int): LiveData<List<MDetail>>

    @Query("SELECT next FROM Next WHERE `movie_id` = :movie_id")
    abstract fun searchNext(movie_id: Int): Int
}