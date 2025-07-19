package com.example.filmsearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmsearch.App
import com.example.filmsearch.domain.Film
import com.example.filmsearch.domain.Interactor
import com.example.filmsearch.utils.SingleLiveEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.Executors
import javax.inject.Inject


class HomeFragmentViewModel : ViewModel() {

    private var currentPage = 1 // Текущая страница для пагинации
    private var isLoading = false
    var showProgressbar: BehaviorSubject<Boolean>
    val hasMoreData: MutableLiveData<Boolean> = MutableLiveData(true) // Флаг для наличия данных
    val errorMessage: SingleLiveEvent<String> =
        SingleLiveEvent()  // Для передачи сообщения об ошибке

    @Inject
    lateinit var interactor: Interactor
    val filmsListData: Observable<List<Film>>

    init {
        App.instance.dagger.inject(this)
        showProgressbar = interactor.progressBarState
        filmsListData = interactor.getFilmsFromDB()
        loadFilms()
    }

    // Метод для загрузки фильмов с пагинацией
    fun loadFilms() {

        interactor.getFilmsFromApi(1)
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}






