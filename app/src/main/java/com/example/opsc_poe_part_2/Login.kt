package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get references to UI elements
        val loginButton = findViewById<Button>(R.id.btnLoggingin)
        val txtEmail = findViewById<EditText>(R.id.editTextEmail)
        val txtPassword = findViewById<EditText>(R.id.editTextPassword)

        // Set the onClick listener for login action
        loginButton.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            // Check if fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    baseContext,
                    "Please enter both email and password.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Hide keyboard after clicking the button
            hideKeyboard()

            // Firebase Authentication
            authenticateUser(email, password)
        }
    }

    private fun authenticateUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Login successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to the next activity (DashboardActivity)
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // Helper function to hide the keyboard
    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
