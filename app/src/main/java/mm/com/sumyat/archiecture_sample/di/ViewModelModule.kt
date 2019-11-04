package mm.com.sumyat.archiecture_sample.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import mm.com.sumyat.archiecture_sample.ui.movie_details.MovieDetailViewModel
import mm.com.sumyat.archiecture_sample.ui.search.SearchViewModel
import mm.com.sumyat.archiecture_sample.viewmodel.SampleViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun bindMDetailViewModel(viemodel : MovieDetailViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SampleViewModelFactory): ViewModelProvider.Factory
}