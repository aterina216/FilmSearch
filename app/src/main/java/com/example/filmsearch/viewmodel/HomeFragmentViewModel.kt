package com.example.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmsearch.App
import com.example.filmsearch.domain.Film
import com.example.filmsearch.domain.Interactor

class HomeFragmentViewModel: ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private var currentPage = 1 // Номер текущей страницы
    private var isLoading = false // Флаг, который предотвращает повторные запросы, пока идет загрузка
    private var interactor: Interactor = App.instance.interactor
    init {
        loadFilms()
    }

    fun loadFilms() {
        if (isLoading) return // Если уже идет загрузка, ничего не делаем
        isLoading = true

        interactor.getFilmsFromApi(currentPage, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                val currentFilms = filmsListLiveData.value.orEmpty()
                filmsListLiveData.postValue(currentFilms + films) // Добавляем новые фильмы к уже загруженным

                currentPage++ // Увеличиваем номер страницы для следующего запроса
                isLoading = false
            }

            override fun onFailure() {
                isLoading = false // В случае ошибки сбрасываем флаг загрузки
            }
        })
    }

    interface ApiCallback{
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}