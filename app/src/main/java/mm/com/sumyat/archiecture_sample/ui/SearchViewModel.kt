package mm.com.sumyat.archiecture_sample.ui

import androidx.lifecycle.*
import mm.com.sumyat.archiecture_sample.repository.RepoRepository
import mm.com.sumyat.archiecture_sample.util.AbsentLiveData
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import javax.inject.Inject


class SearchViewModel @Inject constructor(repository: RepoRepository) : ViewModel(){

    private val _repoId: MutableLiveData<Int> = MutableLiveData()



    val contributors: LiveData<Resource<List<Movie>>> = Transformations
        .switchMap(_repoId) { input ->
            if (input==0) {
                AbsentLiveData.create()
            } else {
                repository.search(input)
            }
        }

    fun retry() {
        val currentPage = _repoId.value
        if (currentPage != null) {
            _repoId.value = currentPage
        }
    }

    fun setId() {
        _repoId.value = + 1
    }
}