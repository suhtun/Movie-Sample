package mm.com.sumyat.archiecture_sample.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import mm.com.sumyat.archiecture_sample.vo.MDetail
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Next

@Database(
    entities = [
        Movie::class, Next::class, MDetail::class],
    version = 3,
    exportSchema = false
)
abstract class SampleDb : RoomDatabase() {
    abstract fun repoDao(): MovieDao
}