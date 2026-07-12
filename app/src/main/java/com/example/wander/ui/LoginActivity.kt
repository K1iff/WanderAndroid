package com.example.wander.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wander.MainActivity
import com.example.wander.R
import com.example.wander.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the two color text for the title
        val titleText = "Wander"
        val spannableString = SpannableString(titleText)

        val orangeColor = ContextCompat.getColor(this, R.color.wander_orange)
        val blueColor = ContextCompat.getColor(this, R.color.wander_blue)

        // Color "Wan" orange (indices 0 to 3)
        spannableString.setSpan(ForegroundColorSpan(orangeColor), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Color "der" blue (indices 3 to 6)
        spannableString.setSpan(ForegroundColorSpan(blueColor), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.titleTextView.text = spannableString

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.createAccountTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}