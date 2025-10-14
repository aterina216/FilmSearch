package com.example.filmsearch.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.filmsearch.R
import com.example.filmsearch.domain.Film
import com.example.filmsearch.view.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "movie_reminders_channel"
        const val CHANNEL_NAME = "Напоминания о фильмах"
        const val CHANNEL_DESCRIPTION = "Канал для напоминаний посмотреть фильмы"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH // Повышаем важность
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}