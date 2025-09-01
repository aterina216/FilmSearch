package com.example.filmsearch.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.filmsearch.R
import com.example.filmsearch.domain.Film
import com.example.filmsearch.view.MainActivity


class MovieNotificationManager(private val context: Context) {

    fun createNotificationChannel() {
        // Создаем канал только для Android 8.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                importance
            ).apply {
                description = NotificationConstants.CHANNEL_DESCRIPTION
            }

            // Получаем системный NotificationManager
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMovieNotification(film: Film) {
        // Создаем Intent для открытия приложения
        val intent = Intent(context, MainActivity::class.java).apply {

            action = "OPEN_MOVIE_DETAILS"
            // Добавляем данные о фильме
            putExtra(NotificationConstants.EXTRA_MOVIE_ID, film.id)
            putExtra(NotificationConstants.EXTRA_MOVIE_TITLE, film.title)
            putExtra(NotificationConstants.EXTRA_MOVIE_POSTER, film.poster)
            putExtra(NotificationConstants.EXTRA_MOVIE_DESCRIPTION, film.description)
            putExtra(NotificationConstants.EXTRA_MOVIE_RATING, film.rating.toString())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Создаем PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            film.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Создаем уведомление
        val notification = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_movie_24)
            .setContentTitle("Посмотреть позже")
            .setContentText(film.title)
            .setContentInfo("Рейтинг: ${film.rating}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Показываем уведомление через системный NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(film.id, notification)
    }
}