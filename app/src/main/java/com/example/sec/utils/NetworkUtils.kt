package com.example.sec.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkUtils {
    private const val BASE_URL = "http://10.0.2.2:5000/api" // Для эмулятора
    // Для реального устройства: "http://192.168.x.x:5000/api"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun makePostRequest(endpoint: String, jsonBody: String): String? {
        return try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL$endpoint")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun makeGetRequest(endpoint: String): String? {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL$endpoint")
                .get()
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}