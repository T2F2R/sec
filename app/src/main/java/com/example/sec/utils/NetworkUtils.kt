package com.example.sec.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkUtils {
    private const val BASE_URL = "http://10.0.2.2:5000/api" // Для эмулятора

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            val isAvailable = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            Log.d("NETWORK_DEBUG", "Проверка сети: $isAvailable")
            isAvailable
        } catch (e: Exception) {
            Log.e("NETWORK_DEBUG", "Ошибка проверки сети: ${e.message}")
            false
        }
    }

    fun makePostRequest(endpoint: String, jsonBody: String): String? {
        return try {
            val url = "$BASE_URL$endpoint"
            Log.d("NETWORK_DEBUG", "🔗 POST запрос: $url")
            Log.d("NETWORK_DEBUG", "📦 Тело запроса: $jsonBody")

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            Log.d("NETWORK_DEBUG", "🔄 Выполнение запроса...")
            val response = client.newCall(request).execute()

            val responseCode = response.code
            val responseBody = response.body?.string()

            Log.d("NETWORK_DEBUG", "📨 Код ответа: $responseCode")
            Log.d("NETWORK_DEBUG", "📄 Тело ответа: $responseBody")
            Log.d("NETWORK_DEBUG", "✅ Запрос выполнен: ${response.isSuccessful}")

            if (!response.isSuccessful) {
                Log.e("NETWORK_DEBUG", "❌ Ошибка HTTP: $responseCode")
            }

            responseBody
        } catch (e: IOException) {
            Log.e("NETWORK_DEBUG", "❌ IO Exception: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            Log.e("NETWORK_DEBUG", "❌ Общая ошибка: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun makeGetRequest(endpoint: String): String? {
        return try {
            val url = "$BASE_URL$endpoint"
            Log.d("NETWORK_DEBUG", "🔗 GET запрос: $url")

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            Log.d("NETWORK_DEBUG", "🔄 Выполнение GET запроса...")
            val response = client.newCall(request).execute()

            val responseCode = response.code
            val responseBody = response.body?.string()

            Log.d("NETWORK_DEBUG", "📨 GET код ответа: $responseCode")
            Log.d("NETWORK_DEBUG", "📄 GET тело ответа: $responseBody")
            Log.d("NETWORK_DEBUG", "✅ GET запрос выполнен: ${response.isSuccessful}")

            if (!response.isSuccessful) {
                Log.e("NETWORK_DEBUG", "❌ GET ошибка HTTP: $responseCode")
            }

            responseBody
        } catch (e: IOException) {
            Log.e("NETWORK_DEBUG", "❌ GET IO Exception: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            Log.e("NETWORK_DEBUG", "❌ GET общая ошибка: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}