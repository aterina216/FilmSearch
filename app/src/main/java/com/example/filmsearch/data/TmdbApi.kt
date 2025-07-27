package com.example.filmsearch.data

import com.example.filmsearch.data.Entity.TmdbResultsDto
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("3/movie/{category}")
    fun getFilms(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Call<TmdbResultsDto>

    @GET("3/search/movie")
    fun getFilmFromSearch(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Observable<TmdbResultsDto>
}
