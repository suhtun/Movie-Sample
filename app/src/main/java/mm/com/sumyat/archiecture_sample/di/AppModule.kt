package mm.com.sumyat.archiecture_sample.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import mm.com.sumyat.archiecture_sample.BuildConfig
import mm.com.sumyat.archiecture_sample.api.SampleService
import mm.com.sumyat.archiecture_sample.db.MovieDao
import mm.com.sumyat.archiecture_sample.db.SampleDb
import mm.com.sumyat.archiecture_sample.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideGithubService(): SampleService {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(logging)
        }
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(SampleService::class.java)
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