package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val registerButton = findViewById<Button>(R.id.btnRegister)
        val txtEmail = findViewById<EditText>(R.id.editTextEmail)
        val txtUsername = findViewById<EditText>(R.id.editTextUsername)
        val txtPassword = findViewById<EditText>(R.id.editTextPassword)
        auth = FirebaseAuth.getInstance()

        // Set the onClick listener for the registration button
        registerButton.setOnClickListener {
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            val username = txtUsername.text.toString()

            // Check if the username is available before creating the account
            checkUsername(username) { isAvailable ->
                if (isAvailable) {
                    createAccount(email, password, username)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Username already taken",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Account created",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.d("Created User", "User created.")

                    executor.execute {
                        try {
                            // Prepare the URL and the connection
                            val url = URL("https://opscmeditationapi.azurewebsites.net/api/users/createUser")
                            val connection = url.openConnection() as HttpURLConnection

                            // Set the request method to POST
                            connection.requestMethod = "POST"
                            connection.doOutput = true
                            connection.setRequestProperty("Content-Type", "application/json")

                            // Create JSON body
                            val jsonInputString = """{"email": "$email", "username": "$username"}"""

                            // Write the JSON body to the output stream
                            OutputStreamWriter(connection.outputStream).use { os ->
                                os.write(jsonInputString)
                                os.flush()
                            }

                            // Check the response code
                            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                                Log.d("RESPONSE", "User created on server")
                            } else {
                                Log.e("RESPONSE", "Error creating user on server")
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        baseContext,
                                        "Failed to create user on server",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("RESPONSE", e.toString())
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    baseContext,
                                    "Sign up failed",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }

                    // Start Login activity after successful registration
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish() // Finish the Register activity to prevent returning
                } else {
                    Toast.makeText(
                        baseContext,
                        "Sign up failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun checkUsername(username: String, callback: (Boolean) -> Unit) {
        executor.execute {
            try {
                val url = URL("https://opscmeditationapi.azurewebsites.net/api/users/CheckUsername?username=$username")
                val json = url.readText()
                val res = Gson().fromJson(json, Response::class.java)
                Log.d("Message", res.message)

                // Call the callback with the result
                callback(res.message == "Username fine")
            } catch (e: Exception) {
                Log.e("CheckUsername", e.toString())
                // If an error occurs, assume username is not available
                callback(false)
            }
        }
    }
}
