package com.example.opsc_poe_part_2

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class LevelUpWorker constructor(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters){
    private lateinit var auth: FirebaseAuth
    override suspend fun doWork(): Result {
        /*
            Code Attribution
            Title: Handling Offline Network Request — WorkManager to the rescue
            Author: Pulkit Aggarwal
            Post Link: https://medium.com/coding-blocks/handling-offline-network-request-workmanager-to-the-rescue-613887cd3034
            Usage: learned how to make api requests offline
        */
        /*
            Code Attribution
            Title: How to use OKHTTP to make a post request in Kotlin?
            Author: heX
            Author Link: https://stackoverflow.com/users/11740298/hex
            Post Link: https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
            Usage: learned how to make patch api requests
        */
        val client = OkHttpClient()
        auth = FirebaseAuth.getInstance()
        val experience = inputData.getString(EXPERIENCE)?.toInt()
        val email = auth.currentUser?.email.toString()
        var resString = ""
        // gets url and adds parameters
        Log.d("Patch stuff", email + experience)

        // gets url and adds parameters
        val url = "https://opscmeditationapi.azurewebsites.net/api/users/updateLevel".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("email", email)
            .addQueryParameter("experience", experience.toString())
            .build()
        Log.d("levelupurl", url.toString())
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")
        // builds request
        val request = Request.Builder().url(url).patch(requestBody).build()
        // does request
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            Log.d("Level Up Response", "Success")
            Result.success()
        } else {
            Log.d("Level Up Response", "Failure: ${response.code}")
            Result.failure()
        }
    }
    companion object {
        var EXPERIENCE = "EXPERIENCE"
    }
}