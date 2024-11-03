package com.example.opsc_poe_part_2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.opsc_poe_part_2.BiometricPromptManager.*

class BiometricAuthActivity : AppCompatActivity() {
    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                val biometricResult by promptManager.promptResult.collectAsState(
                    initial = null
                )
                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        println("Activity result: $it")
                    }
                )
                LaunchedEffect(biometricResult){
                    if(biometricResult is BiometricResult.AuthenticationNotSet){
                        if(Build.VERSION.SDK_INT >= 30){
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )
                            }
                            enrollLauncher.launch(enrollIntent)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {
                        promptManager.showBiometricPrompt(
                            title = "Biometrics required",
                            description = "You are required to use Biometrics to keep your app information safe."
                        )
                    }){
                        Text(text = "Authenticate")
                    }
                    biometricResult?.let { result ->
                        when(result){
                            is BiometricResult.AuthenticationError -> {
                                Text(result.error)
                            }
                            BiometricResult.AuthenticationFailed -> {
                                Text("Authentication Failed")
                            }
                            BiometricResult.AuthenticationNotSet -> {
                                Text("Authentication Not Set")
                            }
                            BiometricResult.AuthenticationSuccess -> {
                                val intent = Intent(this@BiometricAuthActivity, Meditation::class.java)
                                startActivity(intent)
                                finish()
                            }
                            BiometricResult.FeatureUnavailable -> {
                                Text("Biometrics not supported")
                            }
                            BiometricResult.HardwareUnavailable -> {
                                Text("Biometrics not supported")
                            }
                        }
                    }
                }
            }
        }
    }
}

