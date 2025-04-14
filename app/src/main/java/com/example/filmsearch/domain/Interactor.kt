package com.example.filmsearch.domain

import com.example.filmsearch.data.MainRepository

class Interactor(val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDataBase
}