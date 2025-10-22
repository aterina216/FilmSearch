package com.example.filmsearch.utils

object NotificationConstants {
    // Константы для канала уведомлений
    const val CHANNEL_ID = "movie_reminders_channel"
    const val CHANNEL_NAME = "Напоминания о фильмах"
    const val CHANNEL_DESCRIPTION = "Канал для напоминаний посмотреть фильмы"

    // Код запроса разрешений
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123

    // Ключи для передачи данных в Intent
    const val EXTRA_MOVIE_ID = "movie_id"
    const val EXTRA_MOVIE_TITLE = "movie_title"
    const val EXTRA_MOVIE_POSTER = "movie_poster"
    const val EXTRA_MOVIE_DESCRIPTION = "movie_description"
    const val EXTRA_MOVIE_RATING = "movie_rating"

    // ID для уведомлений
    const val NOTIFICATION_ID = 1

    const val FILM_BUNDLE_KEY = "FILM_BUNDLE"
    const val FILM_KEY = "FILM_KEY"
    const val ACTION_REQUEST_SCHEDULE_EXACT_ALARM = "android.settings.REQUEST_SCHEDULE_EXACT_ALARM"
}