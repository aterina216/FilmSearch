package com.example.filmsearch.domain

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Film(
    val title: String,
    val poster: String, //У нас будет приходить ссылка на картинку, так что теперь это String
    val description: String,
    var rating: Double = 0.0, //Приходит не целое число с API
    var isInFavorites: Boolean = false
) : Parcelable
