package com.example.filmsearch.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.filmsearch.App
import com.example.filmsearch.R
import com.example.filmsearch.databinding.ActivityMainBinding
import com.example.filmsearch.domain.Film
import com.example.filmsearch.utils.AppConfig
import com.example.filmsearch.utils.NotificationConstants
import com.example.filmsearch.view.fragments.DetailsFragment
import com.example.filmsearch.view.fragments.FavoritesFragment
import com.example.filmsearch.view.fragments.HomeFragment
import com.example.filmsearch.view.fragments.SelectionsFragment
import com.example.filmsearch.view.fragments.SettingsFragment
import com.example.filmsearch.view.fragments.WathLaterFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val myReceiver = MyReceiver()
    private var originalNightMode: Int? = null
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase Remote Config
        initRemoteConfig()

        originalNightMode = AppCompatDelegate.getDefaultNightMode()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handledIntent = handleIntent(intent)
        initNavigation()

        if (!handledIntent) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_placeholder, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        // Регистрация BroadcastReceiver
        val intentFilters = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        intentFilters.addAction(Intent.ACTION_BATTERY_LOW)
        registerReceiver(myReceiver, intentFilters)

        // Показ промо-экрана
        showPromoIfNeeded()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 2) {
            super.onBackPressed()
        } else {
            showExitDialog()
        }
    }

    private fun initRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun showPromoIfNeeded() {
        if (!App.instance.isPromoShown) {
            Log.d("PROMO_DEBUG", "Checking promo display...")

            firebaseRemoteConfig.fetch()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate()
                        val filmLink = firebaseRemoteConfig.getString("film_link")

                        Log.d("PROMO_DEBUG", "Remote Config film_link: '$filmLink'")
                        Log.d("PROMO_DEBUG", "Is film link blank: ${filmLink.isBlank()}")

                        if (filmLink.isNotBlank()) {
                            App.instance.isPromoShown = true
                            Log.d("PROMO_DEBUG", "Showing promo with link: $filmLink")
                            showPromoView(filmLink)
                        } else {
                            Log.d("PROMO_DEBUG", "Film link is empty, skipping promo")
                        }
                    } else {
                        Log.e("PROMO_DEBUG", "Failed to fetch Remote Config: ${task.exception}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("PROMO_DEBUG", "Remote Config fetch failed: ${exception.message}")
                }
        } else {
            Log.d("PROMO_DEBUG", "Promo already shown, skipping")
       }
    }

    private fun showPromoView(filmLink: String) {
        binding.promoViewGroup.apply {
            visibility = View.VISIBLE
            Log.d("PROMO_DEBUG", "Promo view visibility set to VISIBLE")

            animate()
                .setDuration(1500)
                .alpha(1f)
                .start()

            // Загружаем постер
            setLinkForPoster(filmLink)

            // Обработчик кнопки "Смотреть"
            watchButton.setOnClickListener {
                Log.d("PROMO_DEBUG", "Watch button clicked, hiding promo")
                visibility = View.GONE
            }

            // Добавляем обработчик для самой картинки (на случай если нужно закрыть по клику на постер)
            setOnClickListener {
                Log.d("PROMO_DEBUG", "Promo background clicked, hiding promo")
                visibility = View.GONE
            }
        }
    }

    private fun initNavigation() {
        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    navigateToFragment(HomeFragment(), "home")
                    true
                }
                R.id.favorites -> {
                    if (!AppConfig.isPaidVersion) {
                        showPremiumFeatureToast("Функция \"Избранное\" доступна только в платной версии")
                        false
                    } else {
                        navigateToFragment(FavoritesFragment(), "favorites")
                        true
                    }
                }
                R.id.watch_later -> {
                    navigateToFragment(WathLaterFragment(), "watch_later")
                    true
                }
                R.id.selections -> {
                    if (!AppConfig.isPaidVersion) {
                        showPremiumFeatureToast("Функция \"Подборки\" доступна только в платной версии")
                        false
                    } else {
                        navigateToFragment(SelectionsFragment(), "selections")
                        true
                    }
                }
                R.id.settings -> {
                    navigateToFragment(SettingsFragment(), "settings")
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment, tag: String) {
        val existingFragment = checkFragmentExistence(tag)
        changeFragment(existingFragment ?: fragment, tag)
    }

    private fun showPremiumFeatureToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }

        val fragment = DetailsFragment().apply {
            arguments = bundle
        }

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

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Вы хотите выйти?")
            .setIcon(R.drawable.home_24)
            .setPositiveButton("Да") { _, _ ->
                finish()
            }
            .setNegativeButton("Нет") { _, _ ->
                // Do nothing
            }
            .setNeutralButton("Не знаю") { _, _ ->
                Toast.makeText(this, "Решайся", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun handleIntent(intent: Intent?): Boolean {
        Log.d("MainActivity", "Handling intent: ${intent?.extras}")

        if (intent != null) {
            Log.d("MainActivity", "Intent extras: ${intent.extras?.keySet()}")
            Log.d("MainActivity", "Intent action: ${intent.action}")
            Log.d("MainActivity", "Intent data: ${intent.data}")

            // Проверяем action вместо extra
            if (intent.action == "OPEN_MOVIE_DETAILS") {
                Log.d("MainActivity", "Found action = OPEN_MOVIE_DETAILS")

                // Извлекаем данные о фильме
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
        }
        return false
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