package mm.com.sumyat.archiecture_sample.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import mm.com.sumyat.archiecture_sample.ui.similar_movie.SimilarViewModel
import mm.com.sumyat.archiecture_sample.ui.movies.MoviesViewModel
import mm.com.sumyat.archiecture_sample.viewmodel.SampleViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MoviesViewModel::class)
    abstract fun bindSearchViewModel(moviesViewModel: MoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SimilarViewModel::class)
    abstract fun bindMDetailViewModel(viemodel : SimilarViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SampleViewModelFactory): ViewModelProvider.Factory
}