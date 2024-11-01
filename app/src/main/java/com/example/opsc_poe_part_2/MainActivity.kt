package com.example.opsc_poe_part_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ListenableWorker.Result
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuthUiClient: GoogleAuthUiClient

    // Made using AI
    // check AiWriteUp.md on github for more information
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            // Handle the result of sign-in
            launchSignInWithIntent(data)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, BiometricAuthActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //add video loop
        // Adjust padding for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lbltest)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the register button by ID
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        auth = FirebaseAuth.getInstance()
        googleAuthUiClient = GoogleAuthUiClient(
            context = this,
            oneTapClient = Identity.getSignInClient(this)
        )

        // Set the onClick listener to navigate to the RegisterActivity
        registerButton.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            signInWithGoogle()
        }
        // Find VideoView by ID and set the video background
        val videoView = findViewById<VideoView>(R.id.backgroundVideoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.whalebackround)
        videoView.setVideoURI(videoUri)

        // Start the video and loop it
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }
    }

    // Made using AI
    // check AiWriteUp.md on github for more information
    private fun signInWithGoogle() {
        lifecycleScope.launch {
            try {
                googleAuthUiClient.signIn()?.let { intentSender ->
                    val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                    signInLauncher.launch(intentSenderRequest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Made using AI, but was altered so that our database will save user details.
    // check AiWriteUp.md on github for more information
    private fun launchSignInWithIntent(data: Intent) {
        // Call signInWithIntent to process the result and handle the sign-in
        lifecycleScope.launch {
            val result = googleAuthUiClient.signInWithIntent(data)
            val user = googleAuthUiClient.getSignedInUser()
            val executor = Executors.newSingleThreadExecutor()
            if (result.data != null) {
                executor.execute {
                    val client = OkHttpClient()
                    var url =
                        "https://opscmeditationapi.azurewebsites.net/api/users".toHttpUrlOrNull()!!
                            .newBuilder()
                            .addQueryParameter("email", user?.email)
                            .build()
                    // builds request
                    var request = Request.Builder().url(url).get().build()
                    // does request
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("User Found", "Success")
                        val intent = Intent(this@MainActivity, BiometricAuthActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("User Not Found", "Creating user")

                        //url for request and its parameters
                        url =
                            "https://opscmeditationapi.azurewebsites.net/api/users/createUser".toHttpUrlOrNull()!!
                                .newBuilder()
                                .addQueryParameter("email", user?.email)
                                .addQueryParameter("name", user?.username)
                                .build()
                        val requestBody =
                            RequestBody.create("application/json".toMediaTypeOrNull(), "")
                        // builds request
                        request = Request.Builder().url(url).post(requestBody).build()

                        // makes request and logs outcome
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                Log.d("User not created", "Failure")
                            }

                            override fun onResponse(call: Call, response: okhttp3.Response) {
                                Log.d("User created", "Success")

                                val intent = Intent(this@MainActivity, BiometricAuthActivity::class.java)
                                startActivity(intent)
                            }
                        })
                    }
                }
            } else {
                // Handle error (e.g., show a toast)
                Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

