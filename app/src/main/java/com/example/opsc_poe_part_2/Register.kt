package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
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
        val txtName = findViewById<EditText>(R.id.txtName)
        val txtSurname = findViewById<EditText>(R.id.txtSurname)
        auth = FirebaseAuth.getInstance()

        // Set the onClick listener to navigate to the RegisterActivity
        registerButton.setOnClickListener {
            // Create an intent to start RegisterActivity
            val progressBar : ProgressBar = findViewById(R.id.progressBar)
            checkUser = false
            txtEmail.error = null;
            txtUsername.error = null;
            var check = true

            if(txtEmail.text.toString() == ""){
                txtEmail.error = "Please enter email"
                check = false
            }
            if(txtPassword.text.toString() == ""){
                txtPassword.error = "Please enter password"
                check = false
            }
            if(txtUsername.text.toString() == ""){
                txtUsername.error = "Please enter username"
                check = false
            }
            if(txtName.text.toString() == ""){
                txtName.error = "Please enter name"
                check = false
            }
            if(txtSurname.text.toString() == ""){
                txtSurname.error = "Please enter surname"
                check = false
            }
            if(check){
                /*
                    Code Attribution
                    Title: Async and Await - Kotlin Coroutines
                    Author: Philipp Lackner
                    Link: https://www.youtube.com/watch?v=t-3TOke8tq8
                    Usage: Used to see how to make async functions
                */
                progressBar.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.IO) {

                    val email = txtEmail.text.toString()
                    val password = txtPassword.text.toString()
                    val username = txtUsername.text.toString()
                    val name = txtName.text.toString()
                    val surname = txtSurname.text.toString()
                    val check = async { checkUsername(username) }
                    check.await()
                    val addUser = async { makeUser(email, password, username, name, surname) }
                    addUser.await()
                }
            }
        }
    }
    // checks if username is in use
    fun checkUsername(username: String){
        val txtUsername = findViewById<EditText>(R.id.editTextUsername)
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            // request url
            val url = URL("https://opscmeditationapi.azurewebsites.net/api/users/CheckUsername?username=${username}")
            val json = url.readText()
            // saves response
            val res = Gson().fromJson(json, Response::class.java)
            // if user exsists return error if not allows the makeUser to go through
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
    // makes user
    suspend fun makeUser(email: String, password: String, username: String, name : String, surname: String){
        // delays request to make sure the username check goes through
        delay(3000)
        Log.d("log", "3")
        Log.d("log", checkUser.toString())
        val txtEmail = findViewById<EditText>(R.id.editTextEmail)
        // if check is fine it attempts to make profile
        if(checkUser){
            Log.d("CHECK", "TEST")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // if successful it makes profile
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
                                val client = OkHttpClient()

                                //url for request and its parameters
                                val url = "https://opscmeditationapi.azurewebsites.net/api/users/createUser".toHttpUrlOrNull()!!.newBuilder()
                                    .addQueryParameter("email", email)
                                    .addQueryParameter("name", name)
                                    .addQueryParameter("surname", surname)
                                    .addQueryParameter("username", username)
                                    .build()

                                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")

                                // builds request
                                val request = Request.Builder().url(url).post(requestBody).build()

                                // makes request and logs outcome
                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        Log.d("Patch Response", "Failure")
                                    }

                                    override fun onResponse(call: Call, response: okhttp3.Response) {
                                        Log.d("Patch Response", "Success")
                                    }
                                } )
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
                        //returns error on failure
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
        val progress : ProgressBar = findViewById(R.id.progressBar)
        progress.visibility = View.INVISIBLE
    }
}