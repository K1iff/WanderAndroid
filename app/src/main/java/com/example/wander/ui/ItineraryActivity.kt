package com.example.wander.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wander.MainActivity
import com.example.wander.R
import com.example.wander.api.DeleteItineraryRequest
import com.example.wander.api.WanderRepository
import com.example.wander.databinding.ActivityItineraryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItineraryBinding
    private val repository = WanderRepository()
    private lateinit var adapter: ItineraryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.nav_itinerary

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    val options = android.app.ActivityOptions.makeCustomAnimation(this, 0, 0)
                    startActivity(intent, options.toBundle())
                    true
                }
                R.id.nav_itinerary -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    val options = android.app.ActivityOptions.makeCustomAnimation(this, 0, 0)
                    startActivity(intent, options.toBundle())
                    true
                }
                else -> false
            }
        }

        val sharedPreferences = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("USER_ID", -1)

        if (currentUserId != -1) {
            loadUserItinerary(currentUserId)
        }

        binding.addActivityFab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            val options = android.app.ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
        }
    }

    private fun loadUserItinerary(userId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getUserItinerary(userId)

                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        val rawList = response.data
                        val sortedItineraryList = rawList.sortedWith(compareBy({ it.scheduledDate }, { it.scheduledTime }))

                        if (sortedItineraryList.isEmpty()) {
                            binding.rvItinerary.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        } else {
                            binding.rvItinerary.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        adapter = ItineraryAdapter(sortedItineraryList.toMutableList()) { itineraryId, position ->
                            deleteItem(itineraryId, position)
                        }
                        binding.rvItinerary.adapter = adapter
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun deleteItem(itineraryId: Int, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = DeleteItineraryRequest(itineraryId)
                val response = repository.deleteItinerary(request)

                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        adapter.removeItem(position)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }
}