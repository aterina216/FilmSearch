package com.example.filmsearch.view.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.filmsearch.R
import com.example.filmsearch.databinding.FragmentDetailsBinding
import com.example.filmsearch.domain.Film
import com.example.filmsearch.utils.MovieNotificationManager
import com.example.filmsearch.utils.NotificationConstants
import com.example.filmsearch.utils.NotificationHelper
import com.example.filmsearch.view.MainActivity
import com.example.filmsearch.viewmodel.DetailsFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var film: Film
    private val scope = CoroutineScope(Dispatchers.IO)
    private val viewModel: DetailsFragmentViewModel by viewModels()

    private lateinit var movieNotificationManager: MovieNotificationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        movieNotificationManager = MovieNotificationManager(requireContext().applicationContext)

        film = arguments?.get("film") as Film
        binding.detailsToolbar.title = film.title
        Glide.with(this)
            .load(com.example.core_impl.entity.ApiConstants.IMAGES_URL + "w780" + film.poster)
            .centerCrop()
            .into(binding.detailsPoster)
        binding.detailsDescription.text = film.description

        binding.detailsFabFavorites.setOnClickListener {
            if (!film.isInFavorites) {
                binding.detailsFabFavorites.setImageResource(R.drawable.favorite_base)
                film.isInFavorites = true
            } else {
                binding.detailsFabFavorites.setImageResource(R.drawable.favorite)
                film.isInFavorites = false
            }
        }

        binding.detailsFab.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this film: ${film.title} \n\n ${film.description}"
                )
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }

        binding.detailsFabNotification.setOnClickListener {
            film.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Запрашиваем разрешение
                        requestPermissions(
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            NotificationConstants.NOTIFICATION_PERMISSION_REQUEST_CODE
                        )
                        return@setOnClickListener
                    }
                }

                movieNotificationManager.showMovieNotification(it)
            } ?: run {
                Toast.makeText(requireContext(), "Данные фильма не загружены", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.reminder.setOnClickListener {
            film?.let { currentFilm ->
                // Проверяем разрешения
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Для Android 12+ проверяем разрешение SCHEDULE_EXACT_ALARM
                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        // Запрашиваем разрешение
                        val intent = Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                        return@setOnClickListener
                    }
                }

                // Проверяем разрешение на уведомления (как было раньше)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            NotificationConstants.NOTIFICATION_PERMISSION_REQUEST_CODE
                        )
                        return@setOnClickListener
                    }
                }

                // Если все разрешения есть, показываем диалог выбора времени
                movieNotificationManager.notificationSet(requireContext(), currentFilm)
            } ?: run {
                Toast.makeText(requireContext(), "Данные фильма не загружены", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NotificationConstants.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, показываем уведомление
                film?.let {
                    movieNotificationManager.showMovieNotification(it)
                }
            } else {
                Toast.makeText(requireContext(), "Нужно разрешение на показ уведомлений", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Для Android 10 и выше, разрешение на запись в хранилище не требуется
            true
        } else {
            val result = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.DISPLAY_NAME, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }
            val contentResolver = requireActivity().contentResolver
            val uri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val outputStream = uri?.let { contentResolver.openOutputStream(it) }
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film.title.handleSingleQuote(),
                film.description.handleSingleQuote()
            )
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster() {
        if (!checkPermission()) {
            requestPermission()
            return
        }

        MainScope().launch {
            binding.progressBar.isVisible = true
            val job = scope.async {
                viewModel.loadWallpaper(com.example.core_impl.entity.ApiConstants.IMAGES_URL + "original" + film.poster)
            }

            // Пожадание результата (битмапа) после выполнения async
            val bitmap = job.await()
            saveToGallery(bitmap)

            // После сохранения, убираем прогресс-бар и показываем уведомление
            binding.progressBar.isVisible = false

            Snackbar.make(
                binding.root,
                R.string.downloaded_to_gallery,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.open) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
                .show()
        }
    }
}




