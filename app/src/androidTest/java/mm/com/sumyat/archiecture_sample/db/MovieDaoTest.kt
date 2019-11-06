package mm.com.sumyat.archiecture_sample.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.runner.AndroidJUnit4
import mm.com.sumyat.archiecture_sample.util.LiveDataTestUtil.getValue
import mm.com.sumyat.archiecture_sample.util.TestUtil
import mm.com.sumyat.archiecture_sample.vo.Next
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest : DbTest(){
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertMoviesAndRead() {
        val data = TestUtil.createMovies(10,"Movie")
        db.movieDao().insertMovies(data)
        val loaded = getValue(db.movieDao().getMovies())
        val movie = loaded[0]
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(movie.title, CoreMatchers.`is`("Movie"))
        MatcherAssert.assertThat(movie.overview, CoreMatchers.`is`("this is cartoon movie"))
        MatcherAssert.assertThat(movie.release_date, CoreMatchers.notNullValue())
    }

    @Test
    fun insertDetailsAndRead() {
        val data = TestUtil.createMDetails(10,"Movie",506)
        db.movieDao().insertDetails(data)
        val loaded = getValue(db.movieDao().getDetails(506))
        val detail = loaded[0]
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(detail.title, CoreMatchers.`is`("Movie"))
        MatcherAssert.assertThat(detail.release_date, CoreMatchers.notNullValue())
    }

    @Test
    fun insertNextAndRead() {
        val data = Next(506,1)
        db.movieDao().insertNext(data)
        val loaded = db.movieDao().getNext(506)
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded, CoreMatchers.`is`(1))
    }
}