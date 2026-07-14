package com.example.wander.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wander.api.SaveItineraryRequest
import com.example.wander.api.WanderRepository
import com.example.wander.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val repository = WanderRepository()

    private var destinationId: Int = -1
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("USER_ID", -1)

        destinationId = intent.getIntExtra("DESTINATION_ID", -1)
        binding.detailTitleTextView.text = intent.getStringExtra("DESTINATION_NAME") ?: "Unknown Place"
        binding.detailLocationTextView.text = intent.getStringExtra("DESTINATION_LOCATION") ?: "Unknown Location"
        binding.detailDescriptionTextView.text = intent.getStringExtra("DESTINATION_DESC") ?: ""
        binding.detailHoursTextView.text = intent.getStringExtra("DESTINATION_HOURS") ?: "Always Open"

        val hours = intent.getStringExtra("DESTINATION_HOURS")
        binding.detailHoursTextView.text = if (!hours.isNullOrEmpty()) hours else "Hours not specified"

        val rawImageUrl = intent.getStringExtra("DESTINATION_IMAGE")
        if (!rawImageUrl.isNullOrEmpty()) {
            val fullImageUrl = com.example.wander.api.WanderClient.BASE_URL + rawImageUrl

            Glide.with(this)
                .load(fullImageUrl)
                .centerCrop()
                .into(binding.detailImageView)
        } else {
            binding.detailImageView.setImageDrawable(null)
            binding.detailImageView.setBackgroundColor(android.graphics.Color.parseColor("#DDDDDD"))
        }
        binding.backFab.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            if (destinationId != -1) {
                showDateTimePicker()
            }
        }
    }

    private fun showDayPicker(selectedDate: String, selectedTime: String) {
        val numberPicker = android.widget.NumberPicker(this).apply {
            minValue = 1
            maxValue = 30
            value = 1
            wrapSelectorWheel = false
        }

        AlertDialog.Builder(this)
            .setTitle("Trip Day")
            .setMessage("Which day of your trip is this?")
            .setView(numberPicker)
            .setPositiveButton("Save to Itinerary") { _, _ ->
                saveToDatabase(selectedDate, selectedTime, numberPicker.value)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d:00", hourOfDay, minute)

                showDayPicker(selectedDate, selectedTime)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun saveToDatabase(selectedDate: String, selectedTime: String, dynamicDayNumber: Int) {
        binding.saveButton.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = SaveItineraryRequest(
                    userId = currentUserId,
                    destinationId = destinationId,
                    dayNumber = dynamicDayNumber,
                    scheduledDate = selectedDate,
                    scheduledTime = selectedTime
                )

                val response = repository.saveToItinerary(request)

                withContext(Dispatchers.Main) {
                    binding.saveButton.isEnabled = true
                    if (response.status == "success") {
                        showSuggestionDialog(response.suggestion?.name, response.suggestion?.province)
                    } else {
                        Toast.makeText(this@DetailActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.saveButton.isEnabled = true
                    Toast.makeText(this@DetailActivity, "Failed to save itinerary.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSuggestionDialog(suggestedName: String?, province: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Saved Successfully!")

        if (suggestedName != null) {
            builder.setMessage("Your destination was added to your itinerary.\n\nSince you are visiting $province, you might also like to check out $suggestedName!")
        } else {
            builder.setMessage("Your destination was added to your itinerary.")
        }

        builder.setPositiveButton("Awesome") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}