package com.example.wander.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wander.MainActivity
import com.example.wander.R
import com.example.wander.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.nav_settings

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_itinerary -> {
                    startActivity(Intent(this, ItineraryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Notifications $status", Toast.LENGTH_SHORT).show()
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Dark mode $status", Toast.LENGTH_SHORT).show()
        }

        binding.editProfileText.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.helpCenterText.setOnClickListener {
            Toast.makeText(this, "Opening Help Center", Toast.LENGTH_SHORT).show()
        }

        // Handle Logout
        binding.logoutButton.setOnClickListener {
            getSharedPreferences("WanderPrefs", MODE_PRIVATE).edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
        binding.profileName.text = sharedPrefs.getString("USER_NAME", "Wander Explorer")
        binding.profileEmail.text = sharedPrefs.getString("USER_EMAIL", "explorer@wanderapp.com")
    }
}