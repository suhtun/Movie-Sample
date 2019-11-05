package mm.com.sumyat.archiecture_sample.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import mm.com.sumyat.archiecture_sample.repository.RepoRepository
import mm.com.sumyat.archiecture_sample.ui.movies.MoviesFragment
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class ViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

//    private val repository = Mockito.mock(RepoRepository::class.java)
//    private var repoViewModel = MoviesFragment(repository)
//
//    @Test
//    fun testNull() {
//        MatcherAssert.assertThat(repoViewModel.repo, CoreMatchers.notNullValue())
//        MatcherAssert.assertThat(repoViewModel.contributors, CoreMatchers.notNullValue())
//        Mockito.verify(repository, Mockito.never())
//            .loadRepo(Mockito.anyString(), Mockito.anyString())
//    }
}