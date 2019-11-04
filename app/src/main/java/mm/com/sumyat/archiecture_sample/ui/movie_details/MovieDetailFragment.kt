package mm.com.sumyat.archiecture_sample.ui.movie_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import mm.com.sumyat.archiecture_sample.AppExecutors
import mm.com.sumyat.archiecture_sample.R
import mm.com.sumyat.archiecture_sample.binding.FragmentDataBindingComponent
import mm.com.sumyat.archiecture_sample.databinding.FragmentMovieDetailBinding
import mm.com.sumyat.archiecture_sample.di.Injectable
import mm.com.sumyat.archiecture_sample.ui.search.SearchViewModel
import mm.com.sumyat.archiecture_sample.ui.common.RepoListAdapter
import mm.com.sumyat.archiecture_sample.ui.common.RetryCallback
import mm.com.sumyat.archiecture_sample.util.autoCleared
import mm.com.sumyat.archiecture_sample.vo.Movie
import timber.log.Timber
import javax.inject.Inject

class MovieDetailFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewmodel: MovieDetailViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentMovieDetailBinding>()

    var adapter by autoCleared<RepoListAdapter>()

    private lateinit var gridLayoutManager: GridLayoutManager

    var pastVisibleItems = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private var loading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movie_detail,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val params = MovieDetailFragmentArgs.fromBundle(arguments!!)
        val movie: Movie = Gson().fromJson(params.data, Movie::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.movie = movie

        viewmodel.setQuery(movie.id)

        initRecyclerView()
        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { repo ->
            //            navController().navigate(
//                SearchFragmentDirections.showDetail(Gson().toJson(repo))
//            )
        }

        binding.similarList.isNestedScrollingEnabled = false
        gridLayoutManager = GridLayoutManager(context, 3)
        binding.similarList.layoutManager = gridLayoutManager
        binding.similarList.adapter = rvAdapter
        this.adapter = rvAdapter

        binding.callback = object : RetryCallback {
            override fun retry() {
                viewmodel.refresh()
            }
        }

        binding.loadmore = object : RetryCallback {
            override fun retry() {
                viewmodel.loadNextPage()
            }
        }
    }

    private fun initRecyclerView() {
        binding.similarList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount()
                    totalItemCount = gridLayoutManager.getItemCount()
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition()

                    if (loading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            loading = false
                            Timber.w("loading:scroll${loading}")
                            viewmodel.loadNextPage()
                        }
                    }
                }
            }
        })

//        binding.searchResult = viewmodel.results
        viewmodel.results.observe(viewLifecycleOwner, Observer { result ->
            loading = true
            if (result!= null)
                adapter.submitList(result?.data?.map {
                    Movie(
                        it.id,
                        it.title,
                        it.poster_path,
                        "",
                        "",
                        it.release_date
                    )
                })

        })

        viewmodel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}