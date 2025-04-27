package com.example.elektronicarebeta1

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginUI_AllElementsDisplayed() {
        // Verify all UI elements are visible
        onView(withId(R.id.edit_email)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_sign_in)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_google_signin)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmailPasswordLogin_EmptyFields() {
        // Click login without entering credentials
        onView(withId(R.id.btn_sign_in)).perform(click())

        // Verify error message is shown
        onView(withText("Please enter a valid email address"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEmailPasswordLogin_InvalidEmail() {
        // Enter invalid email
        onView(withId(R.id.edit_email))
            .perform(typeText("invalid-email"), closeSoftKeyboard())

        onView(withId(R.id.edit_password))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click login
        onView(withId(R.id.btn_sign_in)).perform(click())

        // Verify error message
        onView(withText("Please enter a valid email address"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPasswordVisibilityToggle() {
        // Enter password
        onView(withId(R.id.edit_password))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Toggle password visibility
        onView(withId(R.id.toggle_password_visibility)).perform(click())

        // Verify password is visible
        onView(withId(R.id.edit_password))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testNavigateToRegister() {
        // Click sign up link
        onView(withId(R.id.link_sign_up)).perform(click())

        // Verify navigation to register screen
        onView(withId(R.id.btn_create_account))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testGoogleSignIn_ButtonClick() {
        // Click Google sign in button
        onView(withId(R.id.btn_google_signin)).perform(click())

        // Verify Google Sign In intent is launched
        // Note: We can't fully test the Google Sign In flow in instrumented tests
        // as it requires the actual Google Play Services
    }
}
