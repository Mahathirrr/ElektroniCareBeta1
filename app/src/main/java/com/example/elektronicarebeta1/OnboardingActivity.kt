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
            Log.d("OnboardingActivity", "Starting onCreate")
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
            
            navigateToWelcome()
        }
    }

    private fun setupViews() {
        try {
            Log.d("OnboardingActivity", "Finding views")
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
        try {
            Log.d("OnboardingActivity", "Setting up ViewPager")
            val adapter = OnboardingAdapter(onboardingPages)
            viewPager.adapter = adapter
            
            // Enable user swiping
            viewPager.isUserInputEnabled = true
            
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Log.d("OnboardingActivity", "Page selected: $position")
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
            Log.d("OnboardingActivity", "ViewPager setup completed")
        } catch (e: Exception) {
            Log.e("OnboardingActivity", "Error setting up ViewPager", e)
            throw e
        }
    }

    private fun setupButtons() {
        try {
            Log.d("OnboardingActivity", "Setting up buttons")
            nextButton.setOnClickListener {
                Log.d("OnboardingActivity", "Next button clicked, current item: ${viewPager.currentItem}")
                if (viewPager.currentItem < onboardingPages.size - 1) {
                    viewPager.currentItem++
                } else {
                    // Mark onboarding as completed
                    Log.d("OnboardingActivity", "Onboarding completed, navigating to Welcome")
                    val prefs = getSharedPreferences("ElektroniCare", MODE_PRIVATE)
                    prefs.edit().putBoolean("isFirstLaunch", false).apply()
                    
                    navigateToWelcome()
                }
            }

            skipButton.setOnClickListener {
                Log.d("OnboardingActivity", "Skip button clicked")
                // Mark onboarding as completed even if skipped
                val prefs = getSharedPreferences("ElektroniCare", MODE_PRIVATE)
                prefs.edit().putBoolean("isFirstLaunch", false).apply()
                
                navigateToWelcome()
            }
            Log.d("OnboardingActivity", "Buttons setup completed")
        } catch (e: Exception) {
            Log.e("OnboardingActivity", "Error setting up buttons", e)
            throw e
        }
    }

    private fun navigateToWelcome() {
        try {
            Log.d("OnboardingActivity", "Navigating to WelcomeActivity")
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("OnboardingActivity", "Error navigating to Welcome", e)
            Toast.makeText(this, "Error navigating to Welcome: ${e.message}", Toast.LENGTH_LONG).show()
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
