package com.example.wander.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wander.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sexOptions = arrayOf("Male", "Female", "Prefer not to say")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sexOptions)
        binding.sexDropdown.setAdapter(adapter)

        binding.backButtonTextView.setOnClickListener {
            finish()
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.editText?.text.toString()
            val birthdate = binding.birthdateInput.editText?.text.toString()
            val sex = binding.sexDropdown.text.toString()
            val location = binding.locationInput.editText?.text.toString()
            val email = binding.emailInput.editText?.text.toString()
            val password = binding.passwordInput.editText?.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && birthdate.isNotEmpty() && sex.isNotEmpty() && location.isNotEmpty()) {
                Toast.makeText(this, "All fields collected for database insertion", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}