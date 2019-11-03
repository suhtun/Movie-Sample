package mm.com.sumyat.archiecture_sample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mm.com.sumyat.archiecture_sample.MainActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}