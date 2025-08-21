package com.example.filmsearch

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import com.example.core_impl.DaggerRemoteComponent
import com.example.filmsearch.di.AppComponent
//import com.example.filmsearch.di.DI
import com.example.filmsearch.di.DaggerAppComponent
import com.example.filmsearch.di.modules.DatabaseModule
import com.example.filmsearch.di.modules.DomainModule
import com.example.core_impl.RemoteModule

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

    }


    companion object{
        lateinit var instance: App
            private set
    }


}