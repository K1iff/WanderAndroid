package com.example.wander

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wander.api.WanderRepository
import com.example.wander.databinding.ActivityMainBinding
import com.example.wander.ui.DestinationAdapter
import com.example.wander.ui.EditProfileActivity
import com.example.wander.ui.ItineraryActivity
import com.example.wander.ui.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.text.Editable
import android.text.TextWatcher
import com.example.wander.api.Destination
import java.util.Locale
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var masterDestinationList = listOf<Destination>()
    private val repository = WanderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
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

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase(Locale.getDefault())
                filterDestinations(query)
            }
        })

        binding.profileIcon.setOnClickListener {
            val sharedPrefs = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
            val name = sharedPrefs.getString("USER_NAME", "Explorer")
            val email = sharedPrefs.getString("USER_EMAIL", "No Email")

            AlertDialog.Builder(this)
                .setTitle("Traveler Profile")
                .setMessage("Name: $name\nEmail: $email")
                .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                .setNeutralButton("Edit Profile") { _, _ ->
                    startActivity(Intent(this@MainActivity, EditProfileActivity::class.java))
                }
                .show()
        }

        loadDestinations()
        loadUserStats()
    }

    private fun filterDestinations(query: String) {
        val filteredList = masterDestinationList.filter {
            it.name.lowercase(Locale.getDefault()).contains(query) ||
                    it.location.lowercase(Locale.getDefault()).contains(query)
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No location found. Try something else!", Toast.LENGTH_SHORT).show()
        }

        (binding.rvDestinations.adapter as? DestinationAdapter)?.updateList(filteredList)
    }

    private fun loadDestinations() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getDestinations()

                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        val destinationList = response.data

                        masterDestinationList = destinationList

                        Log.d("WANDER_API", "Successfully loaded ${destinationList.size} spots from Luzon!")

                        binding.rvDestinations.layoutManager = LinearLayoutManager(this@MainActivity)

                        val adapter = DestinationAdapter(masterDestinationList)
                        binding.rvDestinations.adapter = adapter

                    } else {
                        Toast.makeText(this@MainActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Connection failed. Is XAMPP running?", Toast.LENGTH_LONG).show()
                    Log.e("WANDER_API", "Network Error", e)
                }
            }
        }
    }
    private fun loadUserStats() {
        val sharedPrefs = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
        val currentUserId = sharedPrefs.getInt("USER_ID", -1)

        if (currentUserId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = repository.getUserStats(currentUserId)

                    withContext(Dispatchers.Main) {
                        if (response.status == "success") {
                            // Update the blue card text dynamically!
                            binding.dashSaved.text = "Saved Itineraries: ${response.savedCount}"
                            binding.dashPlaces.text = "Places Visited: ${response.placesVisited}"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WANDER_API", "Failed to load stats", e)
                }
            }
        }
    }
}