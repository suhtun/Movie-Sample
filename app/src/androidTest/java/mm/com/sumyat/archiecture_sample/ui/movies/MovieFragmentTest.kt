package mm.com.sumyat.archiecture_sample.ui.movies

import androidx.databinding.DataBindingComponent
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.gson.Gson
import mm.com.sumyat.archiecture_sample.R
import mm.com.sumyat.archiecture_sample.binding.FragmentBindingAdapters
import mm.com.sumyat.archiecture_sample.testing.SingleFragmentActivity
import mm.com.sumyat.archiecture_sample.util.*
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class MovieFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var viewModel: MoviesViewModel
    private val results = MutableLiveData<Resource<List<Movie>>>()
    private val loadMoreStatus = MutableLiveData<MoviesViewModel.LoadMoreState>()
    private val searchFragment =
        TestMovieFragment()

    @Before
    fun init() {
        viewModel = Mockito.mock(MoviesViewModel::class.java)
        Mockito.doReturn(loadMoreStatus).`when`(viewModel).loadMoreStatus
        Mockito.`when`(viewModel.results).thenReturn(results)

        mockBindingAdapter = Mockito.mock(FragmentBindingAdapters::class.java)

        searchFragment.appExecutors = countingAppExecutors.appExecutors
        searchFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        searchFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(searchFragment)
        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

    @Test
    fun testLoading() {
        results.postValue(Resource.loading(null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun search() {
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
        viewModel.setQuery("foo")
        Mockito.verify(viewModel).setQuery("foo")
        results.postValue(Resource.loading(null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun loadResults() {
        val movie = TestUtil.createMovie("movie")
        results.postValue(Resource.success(arrayListOf(movie)))
        Espresso.onView(listMatcher().atPosition(0))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("movie"))))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun dataWithLoading() {
        val movie = TestUtil.createMovie("movie")
        results.postValue(Resource.loading(arrayListOf(movie)))
        Espresso.onView(listMatcher().atPosition(0))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("movie"))))
    }

    @Test
    fun error() {
        results.postValue(Resource.error("failed to load", null))
        Espresso.onView(ViewMatchers.withId(R.id.error_msg))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun loadMore() {
        val movies = TestUtil.createMovies(50,"movie")
        results.postValue(Resource.success(movies))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(49)
        Espresso.onView(ViewMatchers.withId(R.id.repo_list)).perform(action)
        Espresso.onView(listMatcher().atPosition(49))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navigateToRepo() {
        Mockito.doNothing().`when`<MoviesViewModel>(viewModel).loadNextPage()
        val movie = TestUtil.createMovie("movie")
        results.postValue(Resource.success(arrayListOf(movie)))
        Espresso.onView(ViewMatchers.withId(R.id.cardview)).perform(ViewActions.click())
        Mockito.verify(searchFragment.navController).navigate(
            MoviesFragmentDirections.showDetail(Gson().toJson(movie))
        )
    }

    @Test
    fun loadMoreProgress() {
        loadMoreStatus.postValue(MoviesViewModel.LoadMoreState(true, null))
        Espresso.onView(ViewMatchers.withId(R.id.load_more_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        loadMoreStatus.postValue(MoviesViewModel.LoadMoreState(false, null))
        Espresso.onView(ViewMatchers.withId(R.id.load_more_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun loadMoreProgressError() {
        loadMoreStatus.postValue(MoviesViewModel.LoadMoreState(true, "QQ"))
        Espresso.onView(ViewMatchers.withText("QQ")).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.repo_list)
    }

    class TestMovieFragment : MoviesFragment() {
        val navController = mock<NavController>()
        override fun navController() = navController
    }
}