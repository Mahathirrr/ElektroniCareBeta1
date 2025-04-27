package com.example.elektronicarebeta1

import android.widget.Button
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class RegisterActivityTest {

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockGoogleSignInClient: GoogleSignInClient

    @Mock
    private lateinit var mockUser: FirebaseUser

    private lateinit var scenario: ActivityScenario<RegisterActivity>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(mockAuth.currentUser).thenReturn(mockUser)

        scenario = ActivityScenario.launch(RegisterActivity::class.java)
    }

    @Test
    fun testRegister_Success() {
        val fullName = "Test User"
        val mobile = "081234567890"
        val email = "test@example.com"
        val password = "password123"

        scenario.onActivity { activity ->
            // Fill in registration fields
            activity.findViewById<EditText>(R.id.edit_full_name).setText(fullName)
            activity.findViewById<EditText>(R.id.edit_mobile).setText(mobile)
            activity.findViewById<EditText>(R.id.edit_email).setText(email)
            activity.findViewById<EditText>(R.id.edit_password).setText(password)

            // Click register button
            activity.findViewById<Button>(R.id.btn_create_account).performClick()

            // Verify Firebase createUserWithEmailAndPassword was called
            verify(mockAuth).createUserWithEmailAndPassword(email, password)
        }
    }

    @Test
    fun testValidateInputs() {
        scenario.onActivity { activity ->
            // Test empty name
            assert(!activity.validateInputs("", "081234567890", "test@example.com", "password123"))

            // Test invalid phone
            assert(!activity.validateInputs("Test User", "123", "test@example.com", "password123"))

            // Test invalid email
            assert(!activity.validateInputs("Test User", "081234567890", "invalid-email", "password123"))

            // Test short password
            assert(!activity.validateInputs("Test User", "081234567890", "test@example.com", "123"))

            // Test valid inputs
            assert(activity.validateInputs("Test User", "081234567890", "test@example.com", "password123"))
        }
    }

    @Test
    fun testPhoneNumberFormatting() {
        scenario.onActivity { activity ->
            // Test various phone number formats
            assert(activity.formatIndonesianPhoneNumber("+6281234567890") == "081234567890")
            assert(activity.formatIndonesianPhoneNumber("6281234567890") == "081234567890")
            assert(activity.formatIndonesianPhoneNumber("81234567890") == "081234567890")
        }
    }

    @Test
    fun testGoogleSignIn_Success() {
        scenario.onActivity { activity ->
            // Click Google Sign In button
            activity.findViewById<Button>(R.id.btn_google_signin).performClick()

            // Verify Google Sign In was triggered
            verify(mockGoogleSignInClient).signInIntent
        }
    }
}
