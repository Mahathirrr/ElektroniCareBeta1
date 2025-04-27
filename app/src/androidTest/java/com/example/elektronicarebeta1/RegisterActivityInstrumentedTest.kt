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
class RegisterActivityInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Test
    fun testRegisterUI_AllElementsDisplayed() {
        onView(withId(R.id.edit_full_name)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_mobile)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_email)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_create_account)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_google_signin)).check(matches(isDisplayed()))
    }

    @Test
    fun testRegister_EmptyFields() {
        // Click register without entering data
        onView(withId(R.id.btn_create_account)).perform(click())

        // Verify error message
        onView(withText("Please enter your full name"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegister_InvalidPhone() {
        // Fill in fields with invalid phone
        onView(withId(R.id.edit_full_name))
            .perform(typeText("Test User"), closeSoftKeyboard())
        onView(withId(R.id.edit_mobile))
            .perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.edit_email))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.edit_password))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click register
        onView(withId(R.id.btn_create_account)).perform(click())

        // Verify error message
        onView(withText("Please enter a valid phone number"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegister_InvalidEmail() {
        // Fill in fields with invalid email
        onView(withId(R.id.edit_full_name))
            .perform(typeText("Test User"), closeSoftKeyboard())
        onView(withId(R.id.edit_mobile))
            .perform(typeText("081234567890"), closeSoftKeyboard())
        onView(withId(R.id.edit_email))
            .perform(typeText("invalid-email"), closeSoftKeyboard())
        onView(withId(R.id.edit_password))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click register
        onView(withId(R.id.btn_create_account)).perform(click())

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
    fun testNavigateToLogin() {
        // Click sign in link
        onView(withId(R.id.link_sign_in)).perform(click())

        // Verify navigation to login screen
        onView(withId(R.id.btn_sign_in))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPhoneNumberFormatting() {
        // Enter phone number
        onView(withId(R.id.edit_mobile))
            .perform(typeText("+6281234567890"), closeSoftKeyboard())

        // Move focus to trigger formatting
        onView(withId(R.id.edit_email)).perform(click())

        // Verify formatted number
        onView(withId(R.id.edit_mobile))
            .check(matches(withText("081234567890")))
    }
}
