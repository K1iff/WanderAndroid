package com.example.wander.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.wander.MainActivity
import com.example.wander.R
import com.example.wander.api.LoginRequest
import com.example.wander.api.WanderRepository
import com.example.wander.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val repository = WanderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val titleText = "Wander"
        val spannableString = SpannableString(titleText)
        val orangeColor = ContextCompat.getColor(this, R.color.wander_orange)
        val blueColor = ContextCompat.getColor(this, R.color.wander_blue)

        spannableString.setSpan(ForegroundColorSpan(orangeColor), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(blueColor), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.titleTextView.text = spannableString

        binding.emailEditText.addTextChangedListener(ClearErrorWatcher(binding.emailInputLayout))
        binding.passwordEditText.addTextChangedListener(ClearErrorWatcher(binding.passwordInputLayout))

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            var isValid = true
            if (email.isEmpty()) {
                binding.emailInputLayout.error = "Email is required"
                isValid = false
            }
            if (password.isEmpty()) {
                binding.passwordInputLayout.error = "Password is required"
                isValid = false
            }

            if (isValid) {
                performLogin(email, password)
            }
        }

        binding.createAccountTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        binding.loginButton.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = repository.login(request)

                withContext(Dispatchers.Main) {
                    binding.loginButton.isEnabled = true
                    if (response.status == "success") {
                        val sharedPreferences = getSharedPreferences("WanderPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        response.userId?.let {
                            editor.putInt("USER_ID", it)
                            editor.putString("USER_NAME", response.fullName)
                            editor.putString("USER_EMAIL", email)

                            if (binding.rememberMeCheckBox.isChecked) {
                                editor.putBoolean("IS_LOGGED_IN", true)
                            }

                            editor.apply()
                        }

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        binding.passwordInputLayout.error = response.message
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.loginButton.isEnabled = true
                    binding.passwordInputLayout.error = "Connection failed."
                }
            }
        }
    }

    inner class ClearErrorWatcher(private val layout: com.google.android.material.textfield.TextInputLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            layout.error = null
        }
        override fun afterTextChanged(s: Editable?) {}
    }
}