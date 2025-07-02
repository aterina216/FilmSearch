package com.example.filmsearch.domain

import androidx.lifecycle.LiveData
import com.bumptech.glide.load.model.ByteArrayLoader
import com.bumptech.glide.load.model.ByteArrayLoader.Converter
import com.example.filmsearch.data.API
import com.example.filmsearch.data.Entity.TmdbResultsDto
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.PreferenceProvider
import com.example.filmsearch.data.TmdbApi
import com.example.filmsearch.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.filmsearch.utils.Conventer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class Interactor(
    private val repo: MainRepository,
    private val retrofitService: TmdbApi,
    private val preferences: PreferenceProvider

) {

    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    val progressBarState = Channel<Boolean>(Channel.CONFLATED)

    // Метод для получения фильмов с API
    fun getFilmsFromApi(page: Int) {

        scope.launch {
            progressBarState.send(true)
        }

        retrofitService.getFilms(
            getDefaultCategoryFromPreferences(),
            API.KEY,
            "ru-RU",
            page // передаем номер страницы
        ).enqueue(object : Callback<TmdbResultsDto> {
            override fun onResponse(
                call: Call<TmdbResultsDto>,
                response: Response<TmdbResultsDto>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val list = Conventer.convertApiListToDtoList(response.body()?.tmdbFilms)

                    scope.launch {
                        // Сохраняем фильмы в базу данных через репозиторий
                        repo.putToDb(list)  // это правильный метод для сохранения фильмов в БД
                        progressBarState.send(false)
                    }
            }}

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                // Обработка ошибок запроса
                scope.launch {
                    progressBarState.send(false)
                }
            }
        })
    }

    // Получаем категорию фильмов из настроек
    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }

    // Сохраняем выбранную категорию в настройках
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Получаем фильмы из базы данных
    fun getFilmsFromDB(): Flow<List<Film>> = repo.getAllFromDB()
}
