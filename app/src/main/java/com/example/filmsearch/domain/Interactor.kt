package com.example.filmsearch.domain

import com.bumptech.glide.load.model.ByteArrayLoader
import com.bumptech.glide.load.model.ByteArrayLoader.Converter
import com.example.filmsearch.data.API
import com.example.filmsearch.data.Entity.TmdbResultsDto
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.TmdbApi
import com.example.filmsearch.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.filmsearch.utils.Conventer


class Interactor(private val repo: MainRepository, private val retrofitService: TmdbApi) {
    //В конструктор мы будм передавать коллбэк из вьюмоделе, чтобы реагировать на то, когда фильмы будут получены
    //и страницу, котороую нужно загрузить (это для пагинации)
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        retrofitService.getFilms(API.KEY, "ru-RU", page).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(call: Call<TmdbResultsDto>, response: Response<TmdbResultsDto>) {
                //При успехе мы вызываем метод передаем onSuccess и в этот коллбэк список фильмов
                callback.onSuccess(Conventer.convertApiListToDtoList(response.body()?.tmdbFilms))
            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                //В случае провала вызываем другой метод коллбека
                callback.onFailure()
            }
        })
    }
}