package com.example.wander.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wander.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Close this screen and go back when the floating button is clicked
        binding.backFab.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            Toast.makeText(this, "Added to your itinerary!", Toast.LENGTH_SHORT).show()
        }
    }
}