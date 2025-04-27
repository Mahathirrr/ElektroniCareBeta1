package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isPasswordVisible = false

    // Indonesian phone number regex pattern
    private val indonesianPhonePattern = Pattern.compile("^(\\+62|62|0)8[1-9][0-9]{6,9}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val fullNameEdit = findViewById<EditText>(R.id.edit_full_name)
        val mobileEdit = findViewById<EditText>(R.id.edit_mobile)
        val emailEdit = findViewById<EditText>(R.id.edit_email)
        val passwordEdit = findViewById<EditText>(R.id.edit_password)
        val createAccountButton = findViewById<Button>(R.id.btn_create_account)
        val togglePasswordVisibility = findViewById<ImageView>(R.id.toggle_password_visibility)
        val backButton = findViewById<View>(R.id.back_button)
        val signInLink = findViewById<TextView>(R.id.link_sign_in)
        val googleSignInButton = findViewById<View>(R.id.btn_google_signin)

        // Format phone number as user types
        mobileEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val number = mobileEdit.text.toString()
                if (number.isNotEmpty()) {
                    mobileEdit.setText(formatIndonesianPhoneNumber(number))
                }
            }
        }

        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEdit.transformationMethod = null
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
            }
            passwordEdit.setSelection(passwordEdit.text.length)
        }

        createAccountButton.setOnClickListener {
            val fullName = fullNameEdit.text.toString().trim()
            val mobile = mobileEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (validateInputs(fullName, mobile, email, password)) {
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
            // Implement Google Sign In
            Toast.makeText(this, "Google Sign In coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(fullName: String, mobile: String, email: String, password: String): Boolean {
        if (fullName.isEmpty()) {
            showError("Mohon masukkan nama lengkap Anda")
            return false
        }
        if (!isValidIndonesianPhoneNumber(mobile)) {
            showError("Mohon masukkan nomor telepon yang valid")
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Mohon masukkan alamat email yang valid")
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            showError("Password minimal 6 karakter")
            return false
        }
        return true
    }

    private fun isValidIndonesianPhoneNumber(phone: String): Boolean {
        return indonesianPhonePattern.matcher(phone).matches()
    }

    private fun formatIndonesianPhoneNumber(number: String): String {
        var formatted = number.replace("[^0-9]".toRegex(), "")

        // Convert all possible prefixes to standard format
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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "mobile" to formatIndonesianPhoneNumber(mobile),
                        "email" to email
                    )

                    user?.let {
                        db.collection("users")
                            .document(it.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                startActivity(Intent(this, DashboardActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                showError("Error menyimpan data: ${e.message}")
                            }
                    }
                } else {
                    showError("Registrasi gagal: ${task.exception?.message}")
                }
            }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
