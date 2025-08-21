package com.example.filmsearch.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.filmsearch.R
import com.example.filmsearch.databinding.ActivityMainBinding
import com.example.filmsearch.domain.Film
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)
        initNavigation()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_placeholder, HomeFragment())
            .addToBackStack(null)
            .commit()

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
                R.id.home ->{
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment?: HomeFragment(), tag)
                    true
                }
                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment?: FavoritesFragment(), tag)
                    true
                }

                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment?: WathLaterFragment(), tag)
                    true
                }

                R.id.selections -> {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment?: SelectionsFragment(), tag)
                    true
                }
                R.id.settings -> {
                    val tag = "settings"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment( fragment?: SettingsFragment(), tag)
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
    private fun checkFragmentExistence(tag: String): Fragment? = supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String){
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
        }
        else {
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
    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                Intent.ACTION_BATTERY_LOW -> {
                    Toast.makeText(context, "Низкий заряд", Toast.LENGTH_SHORT).show()
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    Toast.makeText(context, "Зарядка подключена", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}