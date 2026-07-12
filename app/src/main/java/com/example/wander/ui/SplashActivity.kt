package com.example.wander.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wander.R
import com.example.wander.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the two color text for the Splash Screen title
        val titleText = "Wander"
        val spannableString = SpannableString(titleText)

        val orangeColor = ContextCompat.getColor(this, R.color.wander_orange)
        val blueColor = ContextCompat.getColor(this, R.color.wander_blue)

        spannableString.setSpan(ForegroundColorSpan(orangeColor), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(blueColor), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.appNameTextView.text = spannableString

        // Load and start the animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.logoImageView.startAnimation(fadeIn)
        binding.appNameTextView.startAnimation(fadeIn)
        binding.taglineTextView.startAnimation(fadeIn)

        // Wait for 3 seconds, then transition to the Login Screen
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Apply a smooth crossfade transition built into Android
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            finish()
        }, 3000)
    }
}