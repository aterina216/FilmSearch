package com.example.filmsearch.di.modules

import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.TmdbApi
import com.example.filmsearch.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: TmdbApi) = Interactor(repo = repository,
        retrofitService = tmdbApi)
}