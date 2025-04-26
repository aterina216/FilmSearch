package com.example.filmsearch

import android.app.Application
import com.example.filmsearch.data.ApiConstants
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.TmdbApi
import com.example.filmsearch.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class App: Application() {
    lateinit var repo: MainRepository
    lateinit var interactor: Interactor

    override fun onCreate() {
        super.onCreate()
        //Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        instance = this
        //Инициализируем репозиторий
        repo = MainRepository()

        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG){
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val retrofitService = retrofit.create(TmdbApi::class.java)
        //Инициализируем интерактор
        interactor = Interactor(repo, retrofitService)
    }

    companion object {
        //Здесь статически хранится ссылка на экземпляр App
        lateinit var instance: App
            //Приватный сеттер, чтобы нельзя было в эту переменную присвоить что-либо другое
            private set
    }
}