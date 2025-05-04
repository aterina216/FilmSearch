package com.example.filmsearch.utils

import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt

object AnimationHelper {
    private const val menuItems = 4

    fun performFragmentCircularrevealAnimation(rootView: View, activity: Activity, position: Int){
        Executors.newSingleThreadExecutor().execute{
            while (true){
                if (rootView.isAttachedToWindow){
                    activity.runOnUiThread{
                        val itemCenter = rootView.width / (menuItems * 2)
                        val step = (itemCenter * 2) * (position - 1) + itemCenter
                        val x: Int = step
                        val y: Int = rootView.y.roundToInt() + rootView.height
                        val startRadius = 0
                        val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())
                        ViewAnimationUtils.createCircularReveal(rootView, x, y, startRadius.toFloat(), endRadius.toFloat()).apply {
                            duration = 500
                            interpolator = AccelerateInterpolator()
                            start()
                        }
                        rootView.visibility = View.VISIBLE
                    }
                    return@execute
                }
            }
        }
    }
}