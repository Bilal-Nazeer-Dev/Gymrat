package com.example.gymrat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.gymrat.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Delay for 2 seconds then move to MainActivity (Container for fragments)
        Handler(Looper.getMainLooper()).postDelayed({
            // NOTE: Ensure MainActivity exists (default project creates it).
            // We will set up the Navigation Graph inside MainActivity later.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2000ms = 2 seconds
    }
}