package com.example.filmsearch.data

import android.content.ContentValues
import android.database.Cursor
import com.example.filmsearch.R
import com.example.filmsearch.domain.Film

class MainRepository(dataBaseHelper: DataBaseHelper) {
    private val sqlDB = dataBaseHelper.readableDatabase
    private lateinit var cursor: Cursor

    fun putToDB(film: Film){
        val cv = ContentValues()
        cv.apply {
            put(DataBaseHelper.COLUMN_TITLE, film.title)
            put(DataBaseHelper.COLUMN_POSTER, film.poster)
            put(DataBaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DataBaseHelper.COLUMN_RATING, film.rating)
        }
        sqlDB.insert(DataBaseHelper.TABLE_NAME, null, cv)
    }
    fun getAllFromDB() : List<Film>{
        cursor = sqlDB.rawQuery("SELECT * FROM ${DataBaseHelper.TABLE_NAME}", null)
        val result = mutableListOf<Film>()
        if(cursor.moveToFirst()){
            do {
                val title = cursor.getString(1)
                val poster = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getDouble(4)
                result.add(Film(title, poster, description, rating))
            }
                while (cursor.moveToNext())
        }
        return result
    }

}