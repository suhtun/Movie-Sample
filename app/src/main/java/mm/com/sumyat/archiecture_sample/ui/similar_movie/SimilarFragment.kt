package mm.com.sumyat.archiecture_sample.ui.similar_movie

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
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import mm.com.sumyat.archiecture_sample.AppExecutors
import mm.com.sumyat.archiecture_sample.R
import mm.com.sumyat.archiecture_sample.binding.FragmentDataBindingComponent
import mm.com.sumyat.archiecture_sample.databinding.FragmentMovieDetailBinding
import mm.com.sumyat.archiecture_sample.di.Injectable
import mm.com.sumyat.archiecture_sample.ui.common.RepoListAdapter
import mm.com.sumyat.archiecture_sample.ui.common.RetryCallback
import mm.com.sumyat.archiecture_sample.util.autoCleared
import mm.com.sumyat.archiecture_sample.vo.Movie
import javax.inject.Inject

class SimilarFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewmodel: SimilarViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentMovieDetailBinding>()

    var adapter by autoCleared<RepoListAdapter>()

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
        val params = SimilarFragmentArgs.fromBundle(arguments!!)
        val movie: Movie = Gson().fromJson(params.data, Movie::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.movie = movie

        viewmodel.setQuery(movie.id)

        initRecyclerView()
        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) {}

        binding.similarList.isNestedScrollingEnabled = false
        binding.similarList.layoutManager = GridLayoutManager(context, 3)
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
        binding.searchResult = viewmodel.results
        viewmodel.results.observe(viewLifecycleOwner, Observer { result ->
            if (result != null)
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