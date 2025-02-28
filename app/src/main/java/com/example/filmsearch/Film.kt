package com.example.filmsearch

import android.os.Parcel
import android.os.Parcelable

data class Film(
    val title: String,
    val poster: Int,
    val description: String,
    var isInFavorites: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(title)
        p0.writeInt(poster)
        p0.writeString(description)

    }
    companion object CREATOR : Parcelable.Creator<Film> {
        override fun createFromParcel(parcel: Parcel): Film {
            return Film(parcel)
        }

        override fun newArray(size: Int): Array<Film?> {
            return arrayOfNulls(size)
        }
    }
}