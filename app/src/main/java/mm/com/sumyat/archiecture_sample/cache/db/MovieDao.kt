package mm.com.sumyat.archiecture_sample.cache.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mm.com.sumyat.archiecture_sample.vo.Movie

@Dao
abstract class MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepos(repositories: List<Movie>)

    @Query("SELECT * FROM movie")
    abstract fun search(): LiveData<List<Movie>>

}