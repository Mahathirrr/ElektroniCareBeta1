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

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailEdit = findViewById<EditText>(R.id.edit_email)
        val passwordEdit = findViewById<EditText>(R.id.edit_password)
        val signInButton = findViewById<Button>(R.id.btn_sign_in)
        val togglePasswordVisibility = findViewById<ImageView>(R.id.toggle_password_visibility)
        val backButton = findViewById<View>(R.id.back_button)
        val signUpLink = findViewById<TextView>(R.id.link_sign_up)
        val googleSignInButton = findViewById<View>(R.id.btn_google_signin)

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

        signInButton.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        signUpLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        googleSignInButton.setOnClickListener {
            Toast.makeText(this, "Google Sign In coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Mohon masukkan alamat email yang valid")
            return false
        }
        if (password.isEmpty()) {
            showError("Mohon masukkan password Anda")
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, DashboardActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                } else {
                    showError("Login gagal: Email atau password salah")
                }
            }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
