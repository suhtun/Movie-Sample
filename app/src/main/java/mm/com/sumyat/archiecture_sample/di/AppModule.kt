package mm.com.sumyat.archiecture_sample.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import mm.com.sumyat.archiecture_sample.BuildConfig
import mm.com.sumyat.archiecture_sample.api.NetworkServiceFactory
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.cache.PreferencesHelper
import mm.com.sumyat.archiecture_sample.cache.db.MovieDao
import mm.com.sumyat.archiecture_sample.cache.db.SampleDb
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

//    @Singleton
//    @Provides
//    fun provideContext(application: Application): Context {
//        return application
//    }

    @Singleton
    @Provides
    internal fun provideService(): SampleService {
        return NetworkServiceFactory.makeNetworkService(BuildConfig.DEBUG)
    }

    @Singleton
    @Provides
    internal fun providePreferencesHelper(app: Application): PreferencesHelper {
        return PreferencesHelper(app)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): SampleDb {
        return Room
            .databaseBuilder(app, SampleDb::class.java, "github.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: SampleDb): MovieDao {
        return db.repoDao()
    }
}