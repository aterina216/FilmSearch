package com.example.filmsearch.domain

import com.bumptech.glide.load.model.ByteArrayLoader
import com.example.core_impl.entity.API
import com.example.core_impl.entity.TmdbResultsDto
import com.example.filmsearch.data.MainRepository
import com.example.filmsearch.data.PreferenceProvider
import com.example.core_impl.TmdbApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.filmsearch.utils.Conventer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


class Interactor(
    private val repo: MainRepository,
    private val retrofitService: com.example.core_impl.TmdbApi,
    private val preferences: PreferenceProvider
) {

    var progressBarState: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun getFilmsFromApi(page: Int) {
        //Показываем ProgressBar
        progressBarState.onNext(true)
        //Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page)
            .subscribeOn(Schedulers.io())
            .map {
                Conventer.convertApiListToDtoList(it.tmdbFilms)
            }
            .subscribeBy(
                onError = {
                    progressBarState.onNext(false)
                },
                onNext = {
                    progressBarState.onNext(false)
                    repo.putToDb(it)
                }
            )
    }


    // Метод для получения фильмов с API


    // Получаем категорию фильмов из настроек
    fun getDefaultCategoryFromPreferences(): String {
        return preferences.getDefaultCategory()
    }

    // Сохраняем выбранную категорию в настройках
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Получаем фильмы из базы данных
    fun getFilmsFromDB(): Observable<List<Film>> = repo.getAllFromDB()

    fun getSearchResultFromApi(search: String): Observable<List<Film>> = retrofitService.getFilmFromSearch(
        com.example.core_impl.entity.API.KEY, "ru-RU", search, 1)
       .map{
            Conventer.convertApiListToDtoList(it.tmdbFilms)
       }

}
