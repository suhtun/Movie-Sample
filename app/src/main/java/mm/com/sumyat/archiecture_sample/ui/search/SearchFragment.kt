package mm.com.sumyat.archiecture_sample.ui.search

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import mm.com.sumyat.archiecture_sample.AppExecutors
import mm.com.sumyat.archiecture_sample.R
import mm.com.sumyat.archiecture_sample.binding.FragmentDataBindingComponent
import mm.com.sumyat.archiecture_sample.databinding.FragmentSearchBinding
import mm.com.sumyat.archiecture_sample.di.Injectable
import mm.com.sumyat.archiecture_sample.ui.SearchViewModel
import mm.com.sumyat.archiecture_sample.ui.common.RepoListAdapter
import mm.com.sumyat.archiecture_sample.ui.common.RetryCallback
import mm.com.sumyat.archiecture_sample.util.autoCleared
import mm.com.sumyat.archiecture_sample.vo.Movie
import timber.log.Timber
import javax.inject.Inject

class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewmodel : SearchViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentSearchBinding>()

    var adapter by autoCleared<RepoListAdapter>()

    private fun initContributorList() {
        viewmodel.contributors.observe(viewLifecycleOwner, Observer { listResource ->
            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            }
            else {
                adapter.submitList(emptyList())
            }
        })

        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    viewmodel.setId()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentSearchBinding>(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        dataBinding.callback = object : RetryCallback {
            override fun retry() {
                viewmodel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.setLifecycleOwner(viewLifecycleOwner)
        viewmodel.setId()
        val adapter = RepoListAdapter(dataBindingComponent, appExecutors) { repo ->
//            navController().navigate(
//                SearchFragmentDirections.showRepo(repo.owner.login, repo.name)
//            )
        }
        this.adapter = adapter
        binding.repoList.adapter = adapter

        initContributorList()
    }
}