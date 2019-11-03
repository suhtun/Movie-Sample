package mm.com.sumyat.archiecture_sample.db

import androidx.room.Database
import androidx.room.RoomDatabase
import mm.com.sumyat.archiecture_sample.vo.Movie

@Database(
    entities = [
        Movie::class],
    version = 2,
    exportSchema = false
)
abstract class SampleDb : RoomDatabase() {
    abstract fun repoDao(): MovieDao
}