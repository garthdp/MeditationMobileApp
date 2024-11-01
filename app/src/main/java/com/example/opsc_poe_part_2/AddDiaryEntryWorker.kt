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
import java.text.SimpleDateFormat
import java.util.Date

class AddDiaryEntryWorker constructor(context: Context, workerParameters:WorkerParameters) : CoroutineWorker(context, workerParameters){
    private lateinit var auth: FirebaseAuth
    override suspend fun doWork(): Result {
        /*
            Code Attribution
            Title: Handling Offline Network Request — WorkManager to the rescue
            Author: Pulkit Aggarwal
            Post Link: https://medium.com/coding-blocks/handling-offline-network-request-workmanager-to-the-rescue-613887cd3034
            Usage: learned how to make api requests offline
        */
        val client = OkHttpClient()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        auth = FirebaseAuth.getInstance()
        val content = inputData.getString(CONTENT)
        val title = inputData.getString(TITLE)
        val email = auth.currentUser?.email.toString()
        val color = inputData.getString(COLOR)
        val currentDate = sdf.format(Date())
        var resString = ""
        // gets url and adds parameters

        Log.d("Patch stuff", email + currentDate.toString() + content + title)

        val url = "https://opscmeditationapi.azurewebsites.net/api/Journal".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("email", email)
            .addQueryParameter("date", currentDate)
            .addQueryParameter("content", content)
            .addQueryParameter("color", color)
            .addQueryParameter("title", title)
            .build()
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")
        // builds request
        val request = Request.Builder().url(url).post(requestBody).build()
        // does request
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            Log.d("Patch Response", "Success")
            Result.success()
        } else {
            Log.d("Patch Response", "Failure: ${response.code}")
            Result.failure()
        }
    }
    companion object {
        var CONTENT = "CONTENT"
        var TITLE = "TITLE"
        var COLOR = "COLOR"
    }
}