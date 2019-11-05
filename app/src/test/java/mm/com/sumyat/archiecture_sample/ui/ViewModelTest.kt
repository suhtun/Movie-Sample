package mm.com.sumyat.archiecture_sample.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import mm.com.sumyat.archiecture_sample.repository.RepoRepository
import mm.com.sumyat.archiecture_sample.ui.movies.MoviesViewModel
import mm.com.sumyat.archiecture_sample.util.mock
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class ViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()
    private val repository = mock(RepoRepository::class.java)
    private lateinit var viewModel: MoviesViewModel

    @Before
    fun init() {
        // need to init after instant executor rule is established.
        viewModel = MoviesViewModel(repository)
    }

    @Test
    fun empty() {
        val result = mock<Observer<Resource<List<Movie>>>>()
        viewModel.results.observeForever(result)
        viewModel.loadNextPage()
        Mockito.verifyNoMoreInteractions(repository)
    }

    @Test
    fun basic() {
        val result = mock<Observer<Resource<List<Movie>>>>()
        viewModel.results.observeForever(result)
        viewModel.setQuery("hello")
        verify(repository).searchMovie()
        verify(repository, Mockito.never()).searchNextPage()
    }

    @Test
    fun noObserverNoQuery() {
        Mockito.`when`(repository.searchNextPage()).thenReturn(mock())
        viewModel.setQuery("foo")
        verify(repository, Mockito.never()).searchMovie()
        // next page is user interaction and even if loading state is not observed, we query
        // would be better to avoid that if main search query is not observed
        viewModel.loadNextPage()
        verify(repository).searchNextPage()
    }

    @Test
    fun swap() {
        val nextPage = MutableLiveData<Resource<Boolean>>()
        Mockito.`when`(repository.searchNextPage()).thenReturn(nextPage)

        val result = mock<Observer<Resource<List<Movie>>>>()
        viewModel.results.observeForever(result)
        Mockito.verifyNoMoreInteractions(repository)
        viewModel.setQuery("foo")
        verify(repository).searchMovie()
        viewModel.loadNextPage()

        viewModel.loadMoreStatus.observeForever(mock())
        verify(repository).searchNextPage()
        MatcherAssert.assertThat(nextPage.hasActiveObservers(), CoreMatchers.`is`(true))
        viewModel.setQuery("bar")
        MatcherAssert.assertThat(nextPage.hasActiveObservers(), CoreMatchers.`is`(false))
        verify(repository).searchMovie()
        verify(repository, Mockito.never()).searchNextPage()
    }

    @Test
    fun refresh() {
        viewModel.refresh()
        Mockito.verifyNoMoreInteractions(repository)
        viewModel.setQuery("foo")
        viewModel.refresh()
        Mockito.verifyNoMoreInteractions(repository)
        viewModel.results.observeForever(mock())
        verify(repository).searchMovie()
        Mockito.reset(repository)
        viewModel.refresh()
        verify(repository).searchMovie()
    }

    @Test
    fun resetSameQuery() {
        viewModel.results.observeForever(mock())
        viewModel.setQuery("foo")
        verify(repository).searchMovie()
        Mockito.reset(repository)
        viewModel.setQuery("FOO")
        Mockito.verifyNoMoreInteractions(repository)
        viewModel.setQuery("bar")
        verify(repository).searchMovie()
    }
}