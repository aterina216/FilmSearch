package com.example.filmsearch.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.filmsearch.R
import com.example.filmsearch.databinding.ActivityMainBinding
import com.example.filmsearch.domain.Film
import com.example.filmsearch.utils.NotificationConstants
import com.example.filmsearch.view.fragments.DetailsFragment
import com.example.filmsearch.view.fragments.FavoritesFragment
import com.example.filmsearch.view.fragments.HomeFragment
import com.example.filmsearch.view.fragments.SelectionsFragment
import com.example.filmsearch.view.fragments.SettingsFragment
import com.example.filmsearch.view.fragments.WathLaterFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val myReceiver = MyReceiver()
    private var originalNightMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        originalNightMode = AppCompatDelegate.getDefaultNightMode()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)
        val handledIntent = handleIntent(intent)
        initNavigation()
       if(!handledIntent) {
           supportFragmentManager
               .beginTransaction()
               .add(R.id.fragment_placeholder, HomeFragment())
               .addToBackStack(null)
               .commit()
       }

        val intentFilters = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        intentFilters.addAction(Intent.ACTION_BATTERY_LOW)

        registerReceiver(myReceiver, intentFilters)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }


    fun initNavigation() {
        var topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
        var bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: HomeFragment(), tag)
                    true
                }

                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: FavoritesFragment(), tag)
                    true
                }

                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: WathLaterFragment(), tag)
                    true
                }

                R.id.selections -> {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: SelectionsFragment(), tag)
                    true
                }

                R.id.settings -> {
                    val tag = "settings"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: SettingsFragment(), tag)
                    true
                }

                else -> false
            }
        }
    }

    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)
        val fragment = DetailsFragment()
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 2) {
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Вы хотите выйти?")
                .setIcon(R.drawable.home_24)
                .setPositiveButton("Да") { _, _ ->
                    finish()
                }
                .setNegativeButton("Нет") { _, _ ->

                }
                .setNeutralButton("Не знаю") { _, _ ->
                    Toast.makeText(this, "Решайся", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun handleIntent(intent: Intent?): Boolean {
        Log.d("MainActivity", "Handling intent: ${intent?.extras}")

        if (intent != null) {
            Log.d("MainActivity", "Intent extras: ${intent.extras?.keySet()}")
            Log.d("MainActivity", "Intent action: ${intent.action}")
            Log.d("MainActivity", "Intent data: ${intent.data}")
        }

        // Проверяем action вместо extra
        if (intent != null && intent.action == "OPEN_MOVIE_DETAILS") {
            Log.d("MainActivity", "Found action = OPEN_MOVIE_DETAILS")

            // Извлекаем данные о фильме (обратите внимание на имена ключей!)
            val filmId = intent.getIntExtra("movie_id", -1)
            val filmTitle = intent.getStringExtra("movie_title") ?: ""
            val filmPoster = intent.getStringExtra("movie_poster") ?: ""
            val filmDescription = intent.getStringExtra("movie_description") ?: ""
            val filmRating = intent.getStringExtra("movie_rating")?.toFloatOrNull() ?: 0f

            Log.d("MainActivity", "Film data: id=$filmId, title=$filmTitle")

            if (filmId != -1) {
                // Создаем объект Film
                val film = Film(
                    id = filmId,
                    title = filmTitle,
                    poster = filmPoster,
                    description = filmDescription,
                    rating = filmRating.toDouble(),
                    isInFavorites = false
                )

                // Очищаем back stack и открываем фрагмент с деталями
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                launchDetailsFragment(film)
                return true
            }
        }
        return false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_LOW -> {
                    Toast.makeText(
                        context,
                        "Низкий заряд батареи. Включаем энергосберегающий режим",
                        Toast.LENGTH_LONG
                    ).show()

                    // Сохраняем текущую тему перед изменением
                    if (originalNightMode == null) {
                        originalNightMode = AppCompatDelegate.getDefaultNightMode()
                    }

                    // Включаем темную тему для экономии энергии
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                Intent.ACTION_POWER_CONNECTED -> {
                    Toast.makeText(
                        context,
                        "Зарядка подключена. Восстанавливаем обычный режим",
                        Toast.LENGTH_LONG
                    ).show()

                    // Восстанавливаем исходную тему
                    originalNightMode?.let {
                        AppCompatDelegate.setDefaultNightMode(it)
                    } ?: run {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }

                }

            }
        }

    }
}