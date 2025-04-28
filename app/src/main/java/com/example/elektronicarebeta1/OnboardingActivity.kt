package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
// TabLayoutMediator is not used in this activity
// import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button

    private val onboardingPages = listOf(
        OnboardingPage(
            R.drawable.ic_phone_outline,
            "Phone Repairs",
            "Expert repairs for all smartphone brands with genuine parts"
        ),
        OnboardingPage(
            R.drawable.ic_laptop,
            "Laptop Services",
            "Professional laptop repair and maintenance services"
        ),
        OnboardingPage(
            R.drawable.ic_tv,
            "TV Repairs",
            "Specialized repair for all your TV devices"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_onboarding1)
            Log.d("OnboardingActivity", "Layout set successfully")
            
            setupViews()
            setupViewPager()
            setupButtons()
            
            Toast.makeText(this, "Onboarding started", Toast.LENGTH_SHORT).show()
            Log.d("OnboardingActivity", "Onboarding setup completed")
        } catch (e: Exception) {
            Log.e("OnboardingActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error setting up onboarding: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Fallback to welcome activity if onboarding fails
            val prefs = getSharedPreferences("ElektroniCare", MODE_PRIVATE)
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
            
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun setupViews() {
        try {
            viewPager = findViewById(R.id.viewPager)
            indicatorContainer = findViewById(R.id.indicatorContainer)
            nextButton = findViewById(R.id.nextButton)
            skipButton = findViewById(R.id.skipButton)
            
            Log.d("OnboardingActivity", "Views found successfully")
        } catch (e: Exception) {
            Log.e("OnboardingActivity", "Error finding views", e)
            throw e  // Rethrow to be caught in onCreate
        }
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(onboardingPages)
        viewPager.adapter = adapter
        
        // Disable user swiping to ensure proper navigation
        viewPager.isUserInputEnabled = true
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                updateButtonText(position)
            }
        })

        // Set up indicators
        indicatorContainer.removeAllViews() // Clear any existing indicators
        for (i in onboardingPages.indices) {
            val indicator = View(this).apply {
                setBackgroundResource(R.drawable.circle_purple_bg)
                layoutParams = LinearLayout.LayoutParams(12, 12).apply {
                    marginStart = 8
                    marginEnd = 8
                }
            }
            indicatorContainer.addView(indicator)
        }
        updateIndicators(0)
    }

    private fun setupButtons() {
        nextButton.setOnClickListener {
            if (viewPager.currentItem < onboardingPages.size - 1) {
                viewPager.currentItem++
            } else {
                // Mark onboarding as completed
                val prefs = getSharedPreferences("ElektroniCare", MODE_PRIVATE)
                prefs.edit().putBoolean("isFirstLaunch", false).apply()
                
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        skipButton.setOnClickListener {
            // Mark onboarding as completed even if skipped
            val prefs = getSharedPreferences("ElektroniCare", MODE_PRIVATE)
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
            
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until indicatorContainer.childCount) {
            val indicator = indicatorContainer.getChildAt(i)
            indicator.setBackgroundResource(
                if (i == position) R.drawable.circle_bg
                else R.drawable.circle_purple_bg
            )
        }
    }

    private fun updateButtonText(position: Int) {
        nextButton.text = if (position == onboardingPages.size - 1) "Get Started" else "Next"
    }
}

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)
