package com.example.filmsearch.di.modules

import android.content.Context
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.PreferenceProvider
import com.example.filmsearch.data.TmdbApi
import com.example.filmsearch.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule(val context: Context) {

    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi, preferenceProvider: PreferenceProvider
                          ) = Interactor(repo = repository,
        retrofitService = tmdbApi, preferences = preferenceProvider
    )
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    //Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)
}