package com.example.sec.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sec.R
import com.example.sec.classes.LoginResponse
import com.example.sec.utils.NetworkUtils
import com.google.gson.Gson
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("APP_DEBUG", "LoginActivity onCreate")

        // Проверяем авторизацию
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val employeeId = sharedPref.getInt("employee_id", -1)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

        Log.d("APP_DEBUG", "Проверка авторизации: employeeId=$employeeId, isLoggedIn=$isLoggedIn")

        if (isLoggedIn && employeeId != -1) {
            Log.d("APP_DEBUG", "Пользователь уже авторизован, переход на MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etLogin = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        Log.d("APP_DEBUG", "Views инициализированы")
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            Log.d("APP_DEBUG", "Нажата кнопка входа: login=$login")

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            LoginTask().execute(login, password)
        }

        btnRegister.setOnClickListener {
            Log.d("APP_DEBUG", "Переход на RegisterActivity")
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private inner class LoginTask : AsyncTask<String, Void, LoginResponse>() {

        override fun onPreExecute() {
            progressBar.visibility = ProgressBar.VISIBLE
            btnLogin.isEnabled = false
            Log.d("APP_DEBUG", "Начало авторизации")
        }

        override fun doInBackground(vararg params: String): LoginResponse {
            val login = params[0]
            val password = params[1]

            Log.d("APP_DEBUG", "Отправка запроса на сервер: login=$login")

            try {
                val loginRequest = JSONObject().apply {
                    put("login", login)
                    put("password", password)
                }

                val jsonBody = loginRequest.toString()
                val response = NetworkUtils.makePostRequest("/login", jsonBody)

                Log.d("APP_DEBUG", "Ответ сервера: $response")

                return if (response != null) {
                    gson.fromJson(response, LoginResponse::class.java)
                } else {
                    LoginResponse(success = false, error = "Ошибка подключения к серверу")
                }
            } catch (e: Exception) {
                Log.e("APP_DEBUG", "Ошибка при авторизации: ${e.message}")
                return LoginResponse(success = false, error = "Ошибка приложения")
            }
        }

        override fun onPostExecute(result: LoginResponse) {
            progressBar.visibility = ProgressBar.GONE
            btnLogin.isEnabled = true

            Log.d("APP_DEBUG", "Результат авторизации: success=${result.success}, error=${result.error}")

            if (result.success && result.employeeId != null) {
                Log.d("APP_DEBUG", "Успешная авторизация! ID: ${result.employeeId}")

                // Сохраняем данные
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("employee_id", result.employeeId)
                    putString("employee_name", result.employeeName ?: "Сотрудник")
                    putBoolean("is_logged_in", true)
                    apply()
                }

                Log.d("APP_DEBUG", "Данные сохранены, переход на MainActivity")

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val errorMessage = result.error ?: "Неверный логин или пароль"
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("APP_DEBUG", "Ошибка авторизации: $errorMessage")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("APP_DEBUG", "LoginActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("APP_DEBUG", "LoginActivity onPause")
    }
}