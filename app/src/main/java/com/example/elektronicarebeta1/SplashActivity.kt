package com.example.elektronicarebeta1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d("SplashActivity", "onCreate started")
        auth = FirebaseAuth.getInstance()

        val logo = findViewById<ImageView>(R.id.logoImageView)
        val appName = findViewById<TextView>(R.id.appNameTextView)
        val tagline = findViewById<TextView>(R.id.taglineTextView)

        // Initial state - views are invisible
        logo.alpha = 0f
        appName.alpha = 0f
        tagline.alpha = 0f

        // Create animations
        val logoAnim = createFadeInAnimation(logo, 0)
        val scaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.5f, 1f)
        val appNameAnim = createFadeInAnimation(appName, 300)
        val taglineAnim = createFadeInAnimation(tagline, 600)

        // Combine scale animations
        val scaleAnimSet = AnimatorSet()
        scaleAnimSet.playTogether(scaleX, scaleY)
        scaleAnimSet.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimSet.duration = 500

        // Play all animations together
        AnimatorSet().apply {
            play(logoAnim).with(scaleAnimSet)
            play(appNameAnim).after(logoAnim)
            play(taglineAnim).after(appNameAnim)
            start()
        }

        // Navigate after animations
        android.os.Handler(mainLooper).postDelayed({
            navigateToNextScreen()
        }, 2500)
    }

    private fun createFadeInAnimation(view: View, startDelay: Long): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            duration = 500
            this.startDelay = startDelay
        }
    }

    private fun navigateToNextScreen() {
        try {
            Log.d("SplashActivity", "Navigating to next screen")
            val currentUser = auth.currentUser
            val prefs = getSharedPreferences("ElektroniCare", MODE_PRIVATE)
            val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

            Log.d("SplashActivity", "Current user: ${currentUser?.email}, isFirstLaunch: $isFirstLaunch")

            val intent = when {
                currentUser != null -> {
                    Log.d("SplashActivity", "User is authenticated, navigating to Dashboard")
                    Intent(this, DashboardActivity::class.java)
                }
                isFirstLaunch -> {
                    Log.d("SplashActivity", "First launch, navigating to Onboarding")
                    // We'll set the flag to false in OnboardingActivity when it's completed
                    Intent(this, OnboardingActivity::class.java)
                }
                else -> {
                    Log.d("SplashActivity", "Not first launch, navigating to Welcome")
                    Intent(this, WelcomeActivity::class.java)
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            Log.d("SplashActivity", "Starting activity: ${intent.component?.className}")
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error navigating to next screen", e)
            Toast.makeText(this, "Error navigating to next screen: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Fallback to welcome activity
            try {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } catch (e2: Exception) {
                Log.e("SplashActivity", "Fatal error, could not navigate to any screen", e2)
            }
        }
    }
}
