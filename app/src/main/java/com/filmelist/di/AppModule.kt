package com.filmelist.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.filmelist.BuildConfig
import com.filmelist.data.local.FilmeListDatabase
import com.filmelist.data.local.dao.ListDao
import com.filmelist.data.local.dao.MovieDao
import com.filmelist.data.local.entity.ListEntity
import com.filmelist.data.remote.api.TmdbApi
import com.filmelist.data.repository.ListRepositoryImpl
import com.filmelist.data.repository.MovieRepositoryImpl
import com.filmelist.domain.repository.ListRepository
import com.filmelist.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
                .addHeader("accept", "application/json")
                .build()
            chain.proceed(request)
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApi(client: OkHttpClient): TmdbApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FilmeListDatabase {
        var db: FilmeListDatabase? = null
        db = Room.databaseBuilder(context, FilmeListDatabase::class.java, "filmelist.db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(database: SupportSQLiteDatabase) {
                    super.onCreate(database)
                    CoroutineScope(Dispatchers.IO).launch {
                        db?.listDao()?.insertList(
                            ListEntity(name = "Favoritos de todos os tempos", isDefault = true)
                        )
                    }
                }
            })
            .build()
        return db
    }

    @Provides fun provideListDao(db: FilmeListDatabase): ListDao = db.listDao()
    @Provides fun provideMovieDao(db: FilmeListDatabase): MovieDao = db.movieDao()

    @Provides
    @Singleton
    fun provideMovieRepository(api: TmdbApi): MovieRepository = MovieRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideListRepository(listDao: ListDao, movieDao: MovieDao): ListRepository =
        ListRepositoryImpl(listDao, movieDao)
}
