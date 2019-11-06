package mm.com.sumyat.archiecture_sample.ui.similar

import androidx.databinding.DataBindingComponent
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.gson.Gson
import mm.com.sumyat.archiecture_sample.R
import mm.com.sumyat.archiecture_sample.binding.FragmentBindingAdapters
import mm.com.sumyat.archiecture_sample.testing.SingleFragmentActivity
import mm.com.sumyat.archiecture_sample.ui.movies.MoviesViewModel
import mm.com.sumyat.archiecture_sample.ui.similar_movie.SimilarFragment
import mm.com.sumyat.archiecture_sample.ui.similar_movie.SimilarFragmentArgs
import mm.com.sumyat.archiecture_sample.ui.similar_movie.SimilarViewModel
import mm.com.sumyat.archiecture_sample.util.*
import mm.com.sumyat.archiecture_sample.vo.MDetail
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class SimilarFragmentTest {
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

    private val results = MutableLiveData<Resource<List<MDetail>>>()
    private val loadMoreStatus = MutableLiveData<MoviesViewModel.LoadMoreState>()
    private lateinit var viewModel: SimilarViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val id = 335984
    private val title = "Runner 2049"
    private val poster_path = "/8QXGNP0Vb4nsYKub59XpAhiUSQN.jpg"
    private val vote_average = "7.9"
    private val overview =
        "Thirty years after the events of the first film, a new blade runner, LAPD Officer K, unearths a long-buried secret that has the potential to plunge what's left of society into chaos. K's discovery leads him on a quest to find Rick Deckard, a former LAPD blade runner who has been missing for 30 years."
    private val release_date = "2017-10-04"

    private val searchFragment = TestSimilarFragment().apply {
        val movie = Movie(id, title, poster_path, vote_average, overview, release_date)
        arguments = SimilarFragmentArgs(Gson().toJson(movie)).toBundle()
    }

    @Before
    fun init() {
        viewModel = Mockito.mock(SimilarViewModel::class.java)
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
    fun showMovie() {
        Espresso.onView(ViewMatchers.withText(title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(release_date))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(vote_average))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(overview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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
    fun error() {
        Mockito.doNothing().`when`(viewModel).refresh()
        results.postValue(Resource.error("wtf", null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.error_msg))
            .check(ViewAssertions.matches(ViewMatchers.withText("wtf")))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry)).perform(ViewActions.click())
        Mockito.verify(viewModel).refresh()
    }

    @Test
    fun loadDetails() {
        val repos = setRepos(2)
        for (pos in repos.indices) {
            val repo = repos[pos]
            Espresso.onView(listMatcher().atPosition(pos)).apply {
                check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(repo.title))))
                check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(repo.release_date))))
            }
        }
        val repo3 = setRepos(3)[2]
        Espresso.onView(listMatcher().atPosition(2)).check(
            ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(repo3.title)))
        )
    }

    @Test
    fun nullRepoList() {
        results.postValue(null)
        Espresso.onView(listMatcher().atPosition(0)).check(ViewAssertions.doesNotExist())
    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.similar_list)

    private fun setRepos(count: Int): List<MDetail> {
        val repos = (0 until count).map {
            TestUtil.createMDetail("foo", id)
        }
        results.postValue(Resource.success(repos))
        return repos
    }

    class TestSimilarFragment : SimilarFragment() {
        val navController = mock<NavController>()
        override fun navController() = navController
    }
}