package com.example.wander

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wander.databinding.ActivityMainBinding
import com.example.wander.ui.DetailActivity
import com.example.wander.ui.ItineraryActivity
import com.example.wander.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Highlight the Explore icon in the bottom navigation by default
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        // Click listeners for the placeholder cards
        binding.cardBoracay.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        binding.cardIntramuros.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        // Setup bottom navigation click events to connect all screens
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // The user is already on the Home screen
                    true
                }
                R.id.nav_itinerary -> {
                    val intent = Intent(this, ItineraryActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}