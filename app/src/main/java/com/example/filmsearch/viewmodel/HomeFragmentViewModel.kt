package com.example.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmsearch.App
import com.example.filmsearch.domain.Film
import com.example.filmsearch.domain.Interactor

class HomeFragmentViewModel: ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private var interactor: Interactor = App.instance.interactor
    init {
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsListLiveData.postValue(films)
            }

            override fun onFailure() {
            }
        })
    }
    interface ApiCallback{
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}