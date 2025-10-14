package com.example.filmsearch.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.filmsearch.domain.Film

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.getBundleExtra(NotificationConstants.FILM_BUNDLE_KEY)
        val film: Film = bundle?.get(NotificationConstants.FILM_KEY) as Film

        // Используйте ваш существующий MovieNotificationManager
        val notificationManager = MovieNotificationManager(context!!)
        notificationManager.showMovieNotification(film)
    }
}

