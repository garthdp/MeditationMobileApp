package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val loginButton = findViewById<Button>(R.id.btnLoggingin)
        val txtEmail = findViewById<EditText>(R.id.editTextEmail)
        val txtPassword = findViewById<EditText>(R.id.editTextPassword)
        auth = FirebaseAuth.getInstance()

        // Set the onClick listener to navigate to the RegisterActivity
        loginButton.setOnClickListener {
            // Create an intent to start RegisterActivity

            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Correct details",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this, Questionnaire::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Incorrect details",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }
    }
}