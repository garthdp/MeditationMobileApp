package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

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
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val forgotPassword: TextView = findViewById(R.id.txtForgotPassword)

        forgotPassword.setOnClickListener{
            val email = txtEmail.text.toString()
            if(email != ""){
                ResetPassword(email)
            }
            else{
                txtEmail.error = "Email needed for reset."
            }

        }

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

            // Show progress bar before authentication
            progressBar.visibility = View.VISIBLE

            // Firebase Authentication
            authenticateUser(email, password, progressBar)
        }
    }

    fun ResetPassword(email : String){
        /*
            Code Attribution
            Title: How to create Forgot Password Page in Android Studio using Firebase 🔥
            Author: Android Mate
            Post Link: https://www.youtube.com/watch?v=3AFGljftCzo&t=160s
            Usage: learned how to reset password for firebase
        */
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            Toast.makeText(
                baseContext,
                "Email Sent",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener{
            Toast.makeText(
                baseContext,
                "Please enter valid email",
                Toast.LENGTH_SHORT
            ).show()
            val txtEmail : EditText = findViewById(R.id.editTextEmail)
            txtEmail.error = "Please enter valid email."
        }
    }

    private fun authenticateUser(email: String, password: String, progressBar: ProgressBar) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Hide progress bar after authentication attempt
                progressBar.visibility = View.INVISIBLE

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
