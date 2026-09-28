package com.krushiedge.di

import android.content.Context
import androidx.room.Room
import com.krushiedge.ai.api.AiApiService
import com.krushiedge.ai.local.LocalAiEngine
import com.krushiedge.ai.mapper.AiDtoMapper
import com.krushiedge.ai.provider.AiProvider
import com.krushiedge.ai.provider.HybridAiProvider
import com.krushiedge.ai.provider.LocalAiProvider
import com.krushiedge.ai.provider.MockAiProvider
import com.krushiedge.ai.provider.RemoteAiProvider
import com.krushiedge.ai.validation.AiResponseValidator
import com.krushiedge.data.local.KrushiEdgeDatabase
import com.krushiedge.data.local.dao.AdvisoryDao
import com.krushiedge.data.local.dao.FarmDao
import com.krushiedge.data.local.dao.FieldDao
import com.krushiedge.data.local.dao.MandiDao
import com.krushiedge.data.local.dao.OfflineSyncDao
import com.krushiedge.data.local.dao.ScanRecordDao
import com.krushiedge.data.local.dao.UserDao
import com.krushiedge.data.local.dao.WeatherDao
import com.krushiedge.data.repository.CropDoctorRepositoryImpl
import com.krushiedge.data.repository.FarmRepositoryImpl
import com.krushiedge.data.repository.IrrigationRepositoryImpl
import com.krushiedge.data.repository.MarketRepositoryImpl
import com.krushiedge.data.repository.OfflineSyncRepositoryImpl
import com.krushiedge.data.repository.PestForecastRepositoryImpl
import com.krushiedge.domain.repository.CropDoctorRepository
import com.krushiedge.domain.repository.FarmRepository
import com.krushiedge.domain.repository.IrrigationRepository
import com.krushiedge.domain.repository.MarketRepository
import com.krushiedge.domain.repository.OfflineSyncRepository
import com.krushiedge.domain.repository.PestForecastRepository
import com.krushiedge.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KrushiEdgeDatabase {
        return Room.databaseBuilder(
            context,
            KrushiEdgeDatabase::class.java,
            "krushiedge_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFarmDao(db: KrushiEdgeDatabase): FarmDao = db.farmDao()

    @Provides
    fun provideFieldDao(db: KrushiEdgeDatabase): FieldDao = db.fieldDao()

    @Provides
    fun provideScanRecordDao(db: KrushiEdgeDatabase): ScanRecordDao = db.scanRecordDao()

    @Provides
    fun provideAdvisoryDao(db: KrushiEdgeDatabase): AdvisoryDao = db.advisoryDao()

    @Provides
    fun provideOfflineSyncDao(db: KrushiEdgeDatabase): OfflineSyncDao = db.offlineSyncDao()

    @Provides
    fun provideMandiDao(db: KrushiEdgeDatabase): MandiDao = db.mandiDao()

    @Provides
    fun provideUserDao(db: KrushiEdgeDatabase): UserDao = db.userDao()

    @Provides
    fun provideWeatherDao(db: KrushiEdgeDatabase): WeatherDao = db.weatherDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAiApiService(okHttpClient: OkHttpClient): AiApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.krushiedge.ai/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFarmRepository(impl: FarmRepositoryImpl): FarmRepository

    @Binds
    @Singleton
    abstract fun bindCropDoctorRepository(impl: CropDoctorRepositoryImpl): CropDoctorRepository

    @Binds
    @Singleton
    abstract fun bindIrrigationRepository(impl: IrrigationRepositoryImpl): IrrigationRepository

    @Binds
    @Singleton
    abstract fun bindPestForecastRepository(impl: PestForecastRepositoryImpl): PestForecastRepository

    @Binds
    @Singleton
    abstract fun bindMarketRepository(impl: MarketRepositoryImpl): MarketRepository

    @Binds
    @Singleton
    abstract fun bindOfflineSyncRepository(impl: OfflineSyncRepositoryImpl): OfflineSyncRepository
}
