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
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class LoginActivityTest {

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockGoogleSignInClient: GoogleSignInClient

    @Mock
    private lateinit var mockUser: FirebaseUser

    private lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        scenario = ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun testEmailPasswordLogin_Success() {
        val email = "test@example.com"
        val password = "password123"

        scenario.onActivity { activity ->
            activity.findViewById<EditText>(R.id.edit_email).setText(email)
            activity.findViewById<EditText>(R.id.edit_password).setText(password)
            activity.findViewById<Button>(R.id.btn_sign_in).performClick()

            verify(mockAuth).signInWithEmailAndPassword(eq(email), eq(password))
        }
    }

    @Test
    fun testEmailPasswordLogin_EmptyFields() {
        scenario.onActivity { activity ->
            activity.findViewById<Button>(R.id.btn_sign_in).performClick()
            verify(activity).showError(any())
        }
    }

    @Test
    fun testValidateInputs() {
        scenario.onActivity { activity ->
            assert(!activity.validateInputs("invalid-email", "password"))
            assert(!activity.validateInputs("valid@email.com", ""))
            assert(activity.validateInputs("valid@email.com", "password123"))
        }
    }
}
