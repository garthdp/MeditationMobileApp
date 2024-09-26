package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import kotlinx.coroutines.delay as delay1

var checkUser = false

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val txtEmail = findViewById<EditText>(R.id.editTextEmail)
        val txtUsername = findViewById<EditText>(R.id.editTextUsername)
        val txtPassword = findViewById<EditText>(R.id.editTextPassword)
        auth = FirebaseAuth.getInstance()

        // Set the onClick listener to navigate to the RegisterActivity
        registerButton.setOnClickListener {
            // Create an intent to start RegisterActivity
            checkUser = false
            txtEmail.error = null;
            txtUsername.error = null;

            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            val username = txtUsername.text.toString()

            /*
                Code Attribution
                Title: Async and Await - Kotlin Coroutines
                Author: Philipp Lackner
                Link: https://www.youtube.com/watch?v=t-3TOke8tq8
                Usage: Used to see how to make async functions
            */
            GlobalScope.launch(Dispatchers.IO) {
                val check = async { checkUsername(username) }
                check.await()
                val addUser = async { makeUser(email, password, username) }
                addUser.await()
            }
        }
    }
    fun checkUsername(username: String){
        val txtUsername = findViewById<EditText>(R.id.editTextUsername)
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val url =
                URL("https://opscmeditationapi.azurewebsites.net/api/users/CheckUsername?username=${username}")
            val json = url.readText()
            val res = Gson().fromJson(json, Response::class.java)
            if (res.message == "Username fine") {
                checkUser = true
                Log.d("Message", "Please be fine ${checkUser.toString()}")
            } else {
                Handler(Looper.getMainLooper()).post {
                    txtUsername.error = "Username in use"
                }
            }
        }
        Log.d("log", "2")
    }
    suspend fun makeUser(email: String, password: String, username: String){
        delay(3000)
        Log.d("log", "3")
        Log.d("log", checkUser.toString())
        val txtEmail = findViewById<EditText>(R.id.editTextEmail)
        if(checkUser){
            Log.d("CHECK", "TEST")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Account created",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Log.d("Created User", "User created.")
                        val executor = Executors.newSingleThreadExecutor()
                        executor.execute {
                            try {
                                /*
                                    Code Attribution
                                    Title: Post Request in Kotlin
                                    Author: ChatGPT
                                    Link: https://chatgpt.com/share/66f542f9-8a9c-800f-8529-d5e0c2d5d33d
                                    Usage: Used to see how to make post request in kotlin
                                */
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
                                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                                    val response = connection.inputStream.bufferedReader()
                                        .use { it.readText() }
                                }
                            } catch (e: Exception) {
                                Log.d("RESPONSE", e.toString())
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        baseContext,
                                        "Sign in failed",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        }

                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Sign in failed",
                            Toast.LENGTH_SHORT,
                        ).show()
                        txtEmail.error = "Email in use"
                        Log.d("Error", "Email in use")
                    }
                }
        }
    }
}