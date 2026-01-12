package com.example.gymrat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gymrat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The NavHostFragment in activity_main.xml handles the rest automatically.
    }
}