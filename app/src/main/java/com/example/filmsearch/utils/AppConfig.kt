package com.example.filmsearch.utils

import com.example.filmsearch.BuildConfig

object AppConfig {
    // Просто проверяем, платная ли версия
    val isPaidVersion: Boolean
        get() = BuildConfig.IS_PAID

    // Функции для проверки доступности экранов
    fun isFavoritesAvailable(): Boolean = isPaidVersion
    fun isCollectionsAvailable(): Boolean = isPaidVersion
}