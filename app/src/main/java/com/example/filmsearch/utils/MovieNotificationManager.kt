package com.example.filmsearch.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.filmsearch.R
import com.example.filmsearch.domain.Film
import com.example.filmsearch.view.MainActivity
import java.util.Calendar


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

    fun notificationSet(context: Context, film: Film) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(
            context,
            { _, dpdYear, dpdMonth, dayOfMonth ->
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, pickerMinute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(
                            dpdYear,
                            dpdMonth,
                            dayOfMonth,
                            hourOfDay,
                            pickerMinute,
                            0
                        )
                        val dateTimeInMillis = pickedDateTime.timeInMillis

                        createWatchLaterEvent(context, dateTimeInMillis, film)
                    }

                TimePickerDialog(
                    context,
                    timeSetListener,
                    currentHour,
                    currentMinute,
                    true
                ).show()
            },
            currentYear,
            currentMonth,
            currentDay
        ).show()
    }

    private fun createWatchLaterEvent(context: Context, dateTimeInMillis: Long, film: Film) {
        // Проверяем разрешение для Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "Требуется разрешение на установку точных будильников", Toast.LENGTH_LONG).show()
                return
            }
        }

        // Остальной код метода остается без изменений
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcast::class.java)

        val bundle = Bundle()
        bundle.putParcelable(NotificationConstants.FILM_KEY, film)
        intent.putExtra(NotificationConstants.FILM_BUNDLE_KEY, bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            film.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dateTimeInMillis,
            pendingIntent
        )

        Toast.makeText(context, "Напоминание установлено", Toast.LENGTH_SHORT).show()
    }
}