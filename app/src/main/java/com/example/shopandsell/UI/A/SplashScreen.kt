package com.example.shopandsell.UI.A

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.shopandsell.R


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        val background: Thread = object : Thread() {
            override fun run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep((5 * 1000).toLong())

                    // After 5 seconds redirect to another intent
                    val i = Intent(baseContext, LoginActivity::class.java)
                    startActivity(i)

                    //Remove activity
                    finish()
                } catch (e: Exception) {
                }
            }
        }
        // start thread
        // start thread
        background.start()
    }
}