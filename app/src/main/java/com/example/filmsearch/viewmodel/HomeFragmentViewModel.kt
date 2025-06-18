package com.example.filmsearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmsearch.App
import com.example.filmsearch.domain.Film
import com.example.filmsearch.domain.Interactor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.Executors
import javax.inject.Inject


class HomeFragmentViewModel(// Флаг, который предотвращает повторные запросы, пока идет загрузка
    private var isLoading: Boolean = false
) : ViewModel() {



    private var currentPage = 1 // Номер текущей страницы
    val showProgressbar: MutableLiveData<Boolean> = MutableLiveData()
    @Inject
    lateinit var interactor: Interactor
    val filmsListLiveData: LiveData<List<Film>>

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB()
        loadFilms()
    }

    fun loadFilms() {
        if (isLoading) return // Если уже идет загрузка, ничего не делаем
        isLoading = true

        interactor.getFilmsFromApi(currentPage, object : ApiCallback {
            override fun onSuccess() {

                showProgressbar.postValue(false)
                currentPage++ // Увеличиваем номер страницы для следующего запроса
                isLoading = false
            }

            override fun onFailure() {
                showProgressbar.postValue(false)
            }
        })
    }

    interface ApiCallback{
        fun onSuccess()
        fun onFailure()
    }
}