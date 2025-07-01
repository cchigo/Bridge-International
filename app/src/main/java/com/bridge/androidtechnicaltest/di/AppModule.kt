package com.bridge.androidtechnicaltest.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bridge.androidtechnicaltest.BuildConfig
import com.bridge.androidtechnicaltest.common.Constants.API_TIMEOUT
import com.bridge.androidtechnicaltest.common.Constants.BASE_URL
import com.bridge.androidtechnicaltest.common.Constants.requestId
import com.bridge.androidtechnicaltest.common.Constants.userAgent
import com.bridge.androidtechnicaltest.data.database.AppDatabase
import com.bridge.androidtechnicaltest.data.database.AppDatabase.Companion.DB_NAME
import com.bridge.androidtechnicaltest.data.database.PupilDao
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.model.pupil.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTOMapper
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.domain.PupilRepositoryImpl
import com.bridge.androidtechnicaltest.domain.PupilsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {



    @Provides
    @Singleton
    fun provideRequestInterceptor(): Interceptor {

        return Interceptor { chain ->
            val original = chain.request()
            val newRequest = original.newBuilder()
                .addHeader("X-Request-ID", requestId)
                .addHeader("User-Agent", userAgent)
                .build()
            chain.proceed(newRequest)
        }
    }


    @Provides
    @Singleton
    fun retrofit(requestInterceptor: Interceptor): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(requestInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }


    @Provides
    @Singleton
    fun pupilApi(retrofit: Retrofit): PupilApi {
        return retrofit.create(PupilApi::class.java)
    }

    @Provides
    @Singleton
    fun appDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            DB_NAME
        )
            .build()

    }


    @Provides
    fun providePupilLocalDataSource(pupilDao: PupilDao): PupilLocalDataSource {
        return pupilDao
    }

    @Provides
    fun providePupilDao(appDatabase: AppDatabase): PupilDao {
        return appDatabase.pupilDao()
    }

    //
    @Provides
    fun provideEntityModelMapper(): EntityModelMapper {
        return EntityModelMapper()
    }

    @Provides
    fun provideDTOModelMapper(): PupilDTOMapper {
        return PupilDTOMapper()
    }

    @Provides
     fun bindPupilsRepository(
        impl: PupilRepositoryImpl
    ): PupilsRepository{
         return impl
     }

}
