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


class HomeFragmentViewModel : ViewModel() {

    private var currentPage = 1 // Текущая страница для пагинации
    private var isLoading = false
    val showProgressbar: MutableLiveData<Boolean> = MutableLiveData()
    val hasMoreData: MutableLiveData<Boolean> = MutableLiveData(true) // Флаг для наличия данных
    @Inject
    lateinit var interactor: Interactor
    val filmsListLiveData: LiveData<List<Film>>

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB()
        loadFilms()
    }

    // Метод для загрузки фильмов с пагинацией
    fun loadFilms() {
        if (isLoading) return // Если уже идет загрузка, не запрашиваем снова

        isLoading = true
        showProgressbar.postValue(true)

        // Запрос на сервер с номером страницы
        interactor.getFilmsFromApi(currentPage, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                if (films.isEmpty()) {
                    hasMoreData.postValue(false) // Если фильмов нет, прекращаем пагинацию
                } else {
                    currentPage++ // Увеличиваем номер страницы для следующего запроса
                }

                // Сохраняем фильмы в БД
                // repo.putToDb(films) // Это не нужно, потому что это делаем внутри Interactor
                showProgressbar.postValue(false)
                isLoading = false
            }

            override fun onFailure() {
                showProgressbar.postValue(false)
                isLoading = false
            }
        })
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}
