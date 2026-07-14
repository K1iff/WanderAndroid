package com.example.wander.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wander.api.UpdateProfileRequest
import com.example.wander.api.WanderRepository
import com.example.wander.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val repository = WanderRepository()
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("USER_ID", -1)

        if (currentUserId == -1) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val sexOptions = arrayOf("Male", "Female", "Prefer not to say")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sexOptions)
        binding.sexDropdown.setAdapter(adapter)

        loadUserProfile()

        binding.birthdateInput.editText?.setOnClickListener { showDatePicker() }

        binding.backButtonTextView.setOnClickListener { finish() }

        binding.saveButton.setOnClickListener {
            binding.passwordInput.error = null

            val name = binding.nameInput.editText?.text.toString().trim()
            val birthdate = binding.birthdateInput.editText?.text.toString().trim()
            val sex = binding.sexDropdown.text.toString().trim()
            val location = binding.locationInput.editText?.text.toString().trim()
            val email = binding.emailInput.editText?.text.toString().trim()
            val password = binding.passwordInput.editText?.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || birthdate.isEmpty() || sex.isEmpty() || location.isEmpty()) {
                binding.passwordInput.error = "All fields are required to update profile."
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                binding.passwordInput.error = "Password must be at least 8 characters, include an uppercase, lowercase, number, and special character."
                return@setOnClickListener
            }

            performProfileUpdate(name, birthdate, sex, location, email, password)
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getUserProfile(currentUserId)

                withContext(Dispatchers.Main) {
                    if (response.status == "success" && response.data != null) {
                        binding.nameInput.editText?.setText(response.data.fullName)
                        binding.birthdateInput.editText?.setText(response.data.birthdate)

                        binding.sexDropdown.setText(response.data.sex, false)

                        binding.locationInput.editText?.setText(response.data.location)
                        binding.emailInput.editText?.setText(response.data.email)

                    } else {
                        Toast.makeText(this@EditProfileActivity, "Failed to load profile.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Network error loading profile.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.birthdateInput.editText?.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}\$".toRegex()
        return pattern.matches(password)
    }

    private fun performProfileUpdate(name: String, dob: String, sex: String, loc: String, email: String, pass: String) {
        binding.saveButton.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = UpdateProfileRequest(currentUserId, name, dob, sex, loc, email, pass)
                val response = repository.updateProfile(request)

                withContext(Dispatchers.Main) {
                    binding.saveButton.isEnabled = true

                    if (response.status == "success") {
                        val editor = getSharedPreferences("WanderPrefs", MODE_PRIVATE).edit()
                        editor.putString("USER_NAME", name)
                        editor.putString("USER_EMAIL", email)
                        editor.apply()

                        Toast.makeText(this@EditProfileActivity, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.saveButton.isEnabled = true
                    Toast.makeText(this@EditProfileActivity, "Connection failed. Check network.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}