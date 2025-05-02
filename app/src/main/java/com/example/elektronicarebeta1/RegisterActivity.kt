package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isPasswordVisible = false

    private lateinit var fullNameEdit: EditText
    private lateinit var mobileEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText

    private lateinit var fullNameError: TextView
    private lateinit var mobileError: TextView
    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView

    private val indonesianPhonePattern = Pattern.compile("^(\\+62|62|0)8[1-9][0-9]{6,9}$")

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("RegisterActivity", "Google Sign-In result received: ${result.resultCode}")
        
        if (result.resultCode == RESULT_OK) {
            Log.d("RegisterActivity", "Google Sign-In OK, getting account")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("RegisterActivity", "Google Sign-In successful: ${account.email}")
                account?.idToken?.let {
                    firebaseAuthWithGoogle(it)
                } ?: run {
                    Log.e("RegisterActivity", "ID token is null")
                    Toast.makeText(this, "Google Sign-In failed: ID token is null", Toast.LENGTH_LONG).show()
                    showError(emailError, "Google sign in failed: ID token is null")
                }
            } catch (e: ApiException) {
                // Provide more specific error message based on status code
                val errorMessage = when(e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in was cancelled"
                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Sign in failed"
                    GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Sign in already in progress"
                    GoogleSignInStatusCodes.INVALID_ACCOUNT -> "Invalid account selected"
                    GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> "Sign in required"
                    GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error - check your connection"
                    else -> "Google sign in failed: ${e.statusCode}"
                }
                Log.e("RegisterActivity", "Google sign in failed: ${e.statusCode}", e)
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                showError(emailError, errorMessage)
            }
        } else {
            Log.d("RegisterActivity", "Google Sign-In canceled or failed with code: ${result.resultCode}")
            Toast.makeText(this, "Google Sign-In canceled or failed", Toast.LENGTH_SHORT).show()
            showError(emailError, "Google Sign-In was canceled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        // Log untuk debugging
        Log.d("RegisterActivity", "Firebase Auth initialized: ${auth != null}")
        Log.d("RegisterActivity", "Firebase Firestore initialized: ${db != null}")
        
        // Remove test connection - only access Firestore when needed

        // Konfigurasi Google Sign In
        try {
            // Use the web client ID from the resources
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
                
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            Log.d("RegisterActivity", "Google Sign-In client initialized successfully")
            
            // Check if previously signed in
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                Log.d("RegisterActivity", "User already signed in with Google: ${account.email}")
            }
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Failed to initialize Google Sign-In client", e)
            Toast.makeText(this, "Google Sign-In setup error: ${e.message}", Toast.LENGTH_LONG).show()
            // Don't crash the app if Google Sign-In isn't available
        }

        setupViews()
    }

    // Removed testFirestoreConnection function as it's unnecessary
    // and causes permission issues at startup

    private fun setupViews() {
        fullNameEdit = findViewById(R.id.edit_full_name)
        mobileEdit = findViewById(R.id.edit_mobile)
        emailEdit = findViewById(R.id.edit_email)
        passwordEdit = findViewById(R.id.edit_password)

        fullNameError = findViewById(R.id.fullname_error)
        mobileError = findViewById(R.id.mobile_error)
        emailError = findViewById(R.id.email_error)
        passwordError = findViewById(R.id.password_error)

        val createAccountButton = findViewById<Button>(R.id.btn_create_account)
        val togglePasswordVisibility = findViewById<ImageView>(R.id.toggle_password_visibility)
        val backButton = findViewById<View>(R.id.back_button)
        val signInLink = findViewById<TextView>(R.id.link_sign_in)
        val googleSignInButton = findViewById<View>(R.id.btn_google_signin)

        // Clear errors on focus
        fullNameEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) clearError(fullNameError)
        }

        mobileEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) clearError(mobileError)
            if (!hasFocus) {
                val number = mobileEdit.text.toString()
                if (number.isNotEmpty()) {
                    mobileEdit.setText(formatIndonesianPhoneNumber(number))
                }
            }
        }

        emailEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) clearError(emailError)
        }

        passwordEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) clearError(passwordError)
        }

        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEdit.transformationMethod = null
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            passwordEdit.setSelection(passwordEdit.text.length)
        }

        createAccountButton.setOnClickListener {
            Log.d("RegisterActivity", "Create Account button clicked")
            clearAllErrors()
            val fullName = fullNameEdit.text.toString().trim()
            val mobile = mobileEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (validateInputs(fullName, mobile, email, password)) {
                Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show()
                registerUser(fullName, mobile, email, password)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        signInLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        googleSignInButton.setOnClickListener {
            try {
                Log.d("RegisterActivity", "Google Sign In button clicked")
                Toast.makeText(this, "Starting Google Sign-In...", Toast.LENGTH_SHORT).show()
                signInWithGoogle()
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error during Google sign in button click", e)
                Toast.makeText(this, "Google Sign-In Button Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    public fun validateInputs(fullName: String, mobile: String, email: String, password: String): Boolean {
        var isValid = true

        if (fullName.isEmpty()) {
            showError(fullNameError, "Please enter your full name")
            isValid = false
        }

        if (!isValidIndonesianPhoneNumber(mobile)) {
            showError(mobileError, "Please enter a valid phone number")
            isValid = false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(emailError, "Please enter a valid email address")
            isValid = false
        }

        if (password.isEmpty() || password.length < 6) {
            showError(passwordError, "Password must be at least 6 characters")
            isValid = false
        }

        return isValid
    }

    public fun showError(errorView: TextView, message: String) {
        errorView.text = message
        errorView.visibility = View.VISIBLE
    }

    private fun clearError(errorView: TextView) {
        errorView.text = ""
        errorView.visibility = View.GONE
    }

    private fun clearAllErrors() {
        clearError(fullNameError)
        clearError(mobileError)
        clearError(emailError)
        clearError(passwordError)
    }

    private fun isValidIndonesianPhoneNumber(phone: String): Boolean {
        // Untuk pengujian, kita bisa relax validasi jika diperlukan
        return indonesianPhonePattern.matcher(phone).matches()
        
        // Atau gunakan validasi sederhana untuk testing
        // return phone.isNotEmpty()
    }

    public fun formatIndonesianPhoneNumber(number: String): String {
        var formatted = number.replace("[^0-9]".toRegex(), "")
        if (formatted.startsWith("62")) {
            formatted = "0${formatted.substring(2)}"
        } else if (formatted.startsWith("+62")) {
            formatted = "0${formatted.substring(3)}"
        } else if (!formatted.startsWith("0")) {
            formatted = "0$formatted"
        }
        return formatted
    }

    private fun registerUser(fullName: String, mobile: String, email: String, password: String) {
        Log.d("RegisterActivity", "Attempting to register user with email: $email")
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Log.d("RegisterActivity", "User registration successful: ${authResult.user?.uid}")
                val user = authResult.user
                val userData = hashMapOf(
                    "fullName" to fullName,
                    "mobile" to formatIndonesianPhoneNumber(mobile),
                    "email" to email
                )

                user?.let {
                    Log.d("RegisterActivity", "Saving user data to Firestore: ${it.uid}")
                    db.collection("users")
                        .document(it.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d("RegisterActivity", "User data saved successfully")
                            Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                            navigateToDashboard()
                        }
                        .addOnFailureListener { e ->
                            Log.e("RegisterActivity", "Error saving user data", e)
                            Toast.makeText(this@RegisterActivity, "Error saving data: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Registration failed", e)
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                showError(emailError, "Registration failed: ${e.message}")
            }
    }

    private fun signInWithGoogle() {
        try {
            Log.d("RegisterActivity", "Starting Google Sign-In process")
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error starting Google Sign-In", e)
            Toast.makeText(this, "Google Sign-In Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("RegisterActivity", "Authenticating with Google token")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d("RegisterActivity", "Google authentication successful: ${authResult.user?.uid}")
                val user = authResult.user
                user?.let {
                    val userData = hashMapOf(
                        "fullName" to it.displayName,
                        "email" to it.email,
                        "mobile" to ""
                    )

                    Log.d("RegisterActivity", "Saving Google user data to Firestore: ${it.uid}")
                    db.collection("users")
                        .document(it.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d("RegisterActivity", "Google user data saved successfully")
                            Toast.makeText(this@RegisterActivity, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                            navigateToDashboard()
                        }
                        .addOnFailureListener { e ->
                            Log.e("RegisterActivity", "Error saving Google user data", e)
                            Toast.makeText(this@RegisterActivity, "Error saving user data: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Google authentication failed", e)
                Toast.makeText(this, "Google authentication failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun navigateToDashboard() {
        try {
            Log.d("RegisterActivity", "Creating intent for DashboardActivity")
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            Log.d("RegisterActivity", "Starting DashboardActivity")
            startActivity(intent)
            Log.d("RegisterActivity", "Finishing RegisterActivity")
            finish()
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error navigating to dashboard", e)
            Toast.makeText(this, "Error navigating to dashboard: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
