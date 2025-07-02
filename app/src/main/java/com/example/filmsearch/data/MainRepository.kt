package com.example.filmsearch.data

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import com.example.filmsearch.R
import com.example.filmsearch.data.dao.FilmDao
import com.example.filmsearch.domain.Film
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

class MainRepository(private val filmDao: FilmDao) {
    fun putToDb(films: List<Film>) {

            filmDao.insertAll(films)

    }

    fun getAllFromDB(): Flow<List<Film>> {
        return filmDao.getCachedFilms()
    }
}