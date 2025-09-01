package com.example.filmsearch

import android.app.Application
import com.example.core_impl.DaggerRemoteComponent
import com.example.filmsearch.di.AppComponent
//import com.example.filmsearch.di.DI
import com.example.filmsearch.di.DaggerAppComponent
import com.example.filmsearch.di.modules.DatabaseModule
import com.example.filmsearch.di.modules.DomainModule
import com.example.filmsearch.utils.MovieNotificationManager

class App: Application() {

    lateinit var dagger: AppComponent
    val remoteProvider = DaggerRemoteComponent.create()


    override fun onCreate() {
        super.onCreate()
        //Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        instance = this
        dagger = DaggerAppComponent.builder()
            .remoteProvider(remoteProvider)
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()

        MovieNotificationManager(this).createNotificationChannel()

    }


    companion object{
        lateinit var instance: App
            private set
    }


}