package com.example.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmsearch.App
import com.example.filmsearch.domain.Film
import com.example.filmsearch.domain.Interactor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.inject.Inject


class HomeFragmentViewModel(// Флаг, который предотвращает повторные запросы, пока идет загрузка
    private var isLoading: Boolean = false
) : ViewModel() {


    val filmsListLiveData = MutableLiveData<List<Film>>()
    private var currentPage = 1 // Номер текущей страницы
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
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
                //isLoading = false // В случае ошибки сбрасываем флаг загрузки
                filmsListLiveData.postValue(interactor.getFilmsFromDB())
            }
        })
    }

    interface ApiCallback{
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}