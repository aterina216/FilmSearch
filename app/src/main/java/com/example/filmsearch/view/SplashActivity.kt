package com.example.filmsearch.view

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.filmsearch.R
import com.example.filmsearch.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)


        val rotation = AnimatorInflater.loadAnimator(this, R.animator.logo_animation)
        rotation.setTarget(binding.imageViewLogo)
        rotation.start()

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}