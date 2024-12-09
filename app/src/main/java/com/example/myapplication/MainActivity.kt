package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
    }

    fun click_button(View: View) {
        Toast.makeText(this, "Меню", Toast.LENGTH_SHORT).show()
    }

    fun click_button2(View: View) {
        Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
    }

    fun click_button3(View: View) {
        Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
    }

    fun click_button4(View: View) {
        Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
    }

    fun click_button5(View: View) {
        Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
    }
}