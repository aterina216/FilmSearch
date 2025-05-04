package com.example.filmsearch.utils

import com.example.filmsearch.data.Entity.TmdbFilm
import com.example.filmsearch.domain.Film

object Conventer {
    fun convertApiListToDtoList(list: List<TmdbFilm>?) : List<Film>{
        val result = mutableListOf<Film>()
        list?.forEach{
            result.add(Film(
                title = it.title,
                poster = it.posterPath,
                description = it.overview,
                rating = it.voteAverage,
                isInFavorites = false
            ))
        }
        return result
    }
}