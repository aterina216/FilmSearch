package com.example.filmsearch.utils

import com.example.core_impl.entity.TmdbFilm
import com.example.filmsearch.domain.Film

object Conventer {
    fun convertApiListToDtoList(list: List<com.example.core_impl.entity.TmdbFilm>?) : List<Film>{
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