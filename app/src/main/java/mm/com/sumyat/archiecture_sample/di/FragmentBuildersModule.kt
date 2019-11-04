package mm.com.sumyat.archiecture_sample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mm.com.sumyat.archiecture_sample.ui.similar_movie.SimilarFragment
import mm.com.sumyat.archiecture_sample.ui.movies.MoviesFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): MoviesFragment

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailFragment(): SimilarFragment
}