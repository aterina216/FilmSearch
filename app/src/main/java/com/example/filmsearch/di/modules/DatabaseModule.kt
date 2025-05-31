package com.example.filmsearch.di.modules

import android.content.Context
import com.example.filmsearch.data.MainRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.example.filmsearch.data.DataBaseHelper

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabaseHelper(context: Context) = DataBaseHelper(context)

    @Provides
    @Singleton
    fun provideRepository(databaseHelper: DataBaseHelper) = MainRepository(databaseHelper)
}