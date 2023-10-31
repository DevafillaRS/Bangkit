package com.dicoding.stroyapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.dicoding.stroyapp.R

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            SPLASH_TIME_OUT
        )
    }
}