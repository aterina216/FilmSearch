package com.example.filmsearch.di

import com.example.filmsearch.di.modules.DatabaseModule
import com.example.filmsearch.di.modules.DomainModule
import com.example.filmsearch.di.modules.RemoteModule
import com.example.filmsearch.viewmodel.HomeFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class,
    DatabaseModule::class,
    DomainModule::class])

interface AppComponent {

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
}