package com.example.wander.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wander.api.RegisterRequest
import com.example.wander.api.WanderRepository
import com.example.wander.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val repository = WanderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sexOptions = arrayOf("Male", "Female", "Prefer not to say")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sexOptions)
        binding.sexDropdown.setAdapter(adapter)

        binding.backButtonTextView.setOnClickListener { finish() }

        binding.birthdateInput.editText?.setOnClickListener {
            showDatePicker()
        }
        binding.registerButton.setOnClickListener {
            binding.passwordInput.error = null

            val name = binding.nameInput.editText?.text.toString().trim()
            val birthdate = binding.birthdateInput.editText?.text.toString().trim()
            val sex = binding.sexDropdown.text.toString().trim()
            val location = binding.locationInput.editText?.text.toString().trim()
            val email = binding.emailInput.editText?.text.toString().trim()
            val password = binding.passwordInput.editText?.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || birthdate.isEmpty() || sex.isEmpty() || location.isEmpty()) {
                binding.passwordInput.error = "All fields are required"
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                binding.passwordInput.error = "Password must be at least 8 characters, include an uppercase, lowercase, number, and special character."
                return@setOnClickListener
            }

            performRegistration(name, birthdate, sex, location, email, password)
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
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#_])[A-Za-z\\d@\$!%*?&#_]{8,}\$".toRegex()
        return pattern.matches(password)
    }

    private fun performRegistration(name: String, dob: String, sex: String, loc: String, email: String, pass: String) {
        binding.registerButton.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = RegisterRequest(name, dob, sex, loc, email, pass)
                val response = repository.registerUser(request)

                withContext(Dispatchers.Main) {
                    binding.registerButton.isEnabled = true
                    if (response.status == "success") {
                        finish()
                    } else {
                        binding.emailInput.error = "Email already exists."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.registerButton.isEnabled = true
                    binding.passwordInput.error = e.message
                }
            }
        }
    }
}