package mm.com.sumyat.archiecture_sample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mm.com.sumyat.archiecture_sample.ui.search.SearchFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}