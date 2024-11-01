package com.example.opsc_poe_part_2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors

class Settings : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email.toString()

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        val btnBack = findViewById<Button>(R.id.btnback)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnprofile = findViewById<Button>(R.id.btnprofile)
        val btnPrivacyPolicy = findViewById<Button>(R.id.btnPrivacyPolicy)

       // LanguageTst()
        //language test that breaks

        btnBack.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        val btnThemeSwap = findViewById<Button>(R.id.btnThemeSwap)
        val isDarkMode = loadThemePreference()
        toggleTheme(isDarkMode)
        btnThemeSwap.setOnClickListener {
            // Create an intent to start RegisterActivity
            val newIsDarkMode = !isDarkMode
            saveThemePreference(newIsDarkMode)
            toggleTheme(newIsDarkMode)
        }

        btnprofile.setOnClickListener {
            // Create an intent to start RegisterActivity
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
        btnPrivacyPolicy.setOnClickListener {
            showPrivacyPolicyDialog() //  function to show the privacy policy dialog
        }
        // Initialize BottomNavigationView and set up item selection listener
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_diary -> {
                    startActivity(Intent(this, Dairy::class.java))
                    true
                }
                R.id.nav_meditation -> {
                    startActivity(Intent(this, Meditation::class.java))
                    true
                }
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, Rewards::class.java))
                    true
                }
                R.id.nav_game -> {
                    startActivity(Intent(this, Game::class.java))
                    true
                }
                else -> false
            }
        }

        btnLogout.setOnClickListener{
            handleLogout()
        }
        btnDelete.setOnClickListener{
            handleAccountDelete(email)
        }
    }
    private fun showPrivacyPolicyDialog() {
        val privacyPolicyText = "Here at Oceans Depth your privacy is very important to us.\n" +
                "\n" +
                "Unless you choose to give it to us, our meditation app does not collect you r personal data without your consent. However to improve the app for your use we may collect usage data but this data is anonymized. Third parties do not have access to your data. PLease feel free to send our team a message via email so we can come back to you if you have any concerns or quarries.\n" +
                "\n" +
                "Thank you for using Oceans depth!"
        AlertDialog.Builder(this)
            .setTitle("Privacy Policy")
            .setMessage(privacyPolicyText)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun handleAccountDelete(email: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Yes") { dialog, which ->
                val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                DeleteUser(email)
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
    fun DeleteUser(email : String?) {
        /*
            Code Attribution
            Title: How to use OKHTTP to make a post request in Kotlin?
            Author: heX
            Author Link: https://stackoverflow.com/users/11740298/hex
            Post Link: https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
            Usage: learned how to make patch api requests
        */
        val client = OkHttpClient()
        FirebaseAuth.getInstance().currentUser?.delete()

        // gets url and adds parameters
        val url = "https://opscmeditationapi.azurewebsites.net/api/users/DeleteUser".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("email", email)
            .build()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")

        // builds request
        val request = Request.Builder().url(url).delete(requestBody).build()

        //makes call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Patch Response", "Failure")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                Log.d("Patch Response", "Success")
            }
        } )
    }


    // Toggle between dark/light mode

    private fun toggleTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun loadThemePreference(): Boolean {
        return sharedPreferences.getBoolean("isDarkMode", false)
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
    }

    fun LanguageTst(){

        val lbltest = findViewById<TextView>(R.id.lbltest)
        lbltest.text = R.string.LanguageTest.toString()
       // val context = LocalContext.current
      //  val localeOptions = mapOf(
      //      R.string.en to "en",
      //      R.string.af to "af"
      //  ).mapKeys { stringResource(it.key) }


      //  AppCompatActivity.setApplicationLocales(
     //       LocaleListCompat.forLanguageTags(
      //         localeOptions[R.string.pick_color]
      ///      )
     //   )
    }
}