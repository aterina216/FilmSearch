package com.example.filmsearch

import android.app.Application
import com.example.filmsearch.data.ApiConstants
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.TmdbApi
import com.example.filmsearch.di.DI
import com.example.filmsearch.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        //Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        startKoin{
            androidContext(this@App)
            androidLogger()
            modules(listOf(DI.mainModule))
        }
    }
}