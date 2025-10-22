package com.example.filmsearch.di

import com.example.filmsearch.di.modules.DatabaseModule
import com.example.filmsearch.di.modules.DomainModule
import com.example.core_impl.RemoteModule
import com.example.core_impl.RemoteProvider
import com.example.filmsearch.viewmodel.HomeFragmentViewModel
import com.example.filmsearch.viewmodel.SettingsFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [RemoteProvider::class],
    modules = [DatabaseModule::class, DomainModule::class])

interface AppComponent {

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}