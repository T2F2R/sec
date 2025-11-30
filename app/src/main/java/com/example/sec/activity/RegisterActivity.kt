package com.example.sec.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sec.R
import com.example.sec.classes.RegisterResponse
import com.example.sec.utils.NetworkUtils
import com.google.gson.Gson
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etPatronymic: EditText
    private lateinit var etPassportSeries: EditText
    private lateinit var etPassportNumber: EditText
    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etPatronymic = findViewById(R.id.etPatronymic)
        etPassportSeries = findViewById(R.id.etPassportSeries)
        etPassportNumber = findViewById(R.id.etPassportNumber)
        etLogin = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            val lastName = etLastName.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val patronymic = etPatronymic.text.toString().trim()
            val passportSeries = etPassportSeries.text.toString().trim()
            val passportNumber = etPassportNumber.text.toString().trim()
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (!validateInput(lastName, firstName, passportSeries, passportNumber, login, password, confirmPassword)) {
                return@setOnClickListener
            }

            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RegisterTask().execute(lastName, firstName, patronymic, passportSeries, passportNumber, login, password)
        }
    }

    private fun validateInput(
        lastName: String,
        firstName: String,
        passportSeries: String,
        passportNumber: String,
        login: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (lastName.isEmpty() || firstName.isEmpty() || passportSeries.isEmpty() ||
            passportNumber.isEmpty() || login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
            return false
        }

        if (passportSeries.length != 4) {
            Toast.makeText(this, "Серия паспорта должна содержать 4 цифры", Toast.LENGTH_SHORT).show()
            return false
        }

        if (passportNumber.length != 6) {
            Toast.makeText(this, "Номер паспорта должен содержать 6 цифр", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private inner class RegisterTask : AsyncTask<String, Void, RegisterResponse>() {

        override fun onPreExecute() {
            progressBar.visibility = ProgressBar.VISIBLE
            btnRegister.isEnabled = false
            Log.d("REGISTER_DEBUG", "=== НАЧАЛО РЕГИСТРАЦИИ ===")
        }

        override fun doInBackground(vararg params: String): RegisterResponse {
            val lastName = params[0]
            val firstName = params[1]
            val patronymic = params[2]
            val passportSeries = params[3].toIntOrNull()
            val passportNumber = params[4].toIntOrNull()
            val login = params[5]
            val password = params[6]

            Log.d("REGISTER_DEBUG", "Данные: $lastName $firstName, паспорт: $passportSeries $passportNumber, логин: $login")

            if (passportSeries == null || passportNumber == null) {
                return RegisterResponse(success = false, error = "Неверный формат паспортных данных")
            }

            // Используем JSONObject для точного контроля данных
            val registerRequest = JSONObject().apply {
                put("last_name", lastName)
                put("first_name", firstName)
                put("patronymic", if (patronymic.isEmpty()) "" else patronymic)
                put("passport_series", passportSeries)
                put("passport_number", passportNumber)
                put("login", login)
                put("password", password)
            }

            val jsonBody = registerRequest.toString()
            Log.d("REGISTER_DEBUG", "Отправляемый JSON: $jsonBody")

            val response = NetworkUtils.makePostRequest("/register", jsonBody)

            Log.d("REGISTER_DEBUG", "Ответ сервера: $response")

            return if (response != null) {
                try {
                    val result = gson.fromJson(response, RegisterResponse::class.java)
                    Log.d("REGISTER_DEBUG", "Парсинг ответа: success=${result.success}, error=${result.error}")
                    result
                } catch (e: Exception) {
                    Log.e("REGISTER_DEBUG", "Ошибка парсинга: ${e.message}")
                    RegisterResponse(success = false, error = "Ошибка обработки ответа")
                }
            } else {
                Log.e("REGISTER_DEBUG", "Нет ответа от сервера")
                RegisterResponse(success = false, error = "Ошибка подключения к серверу")
            }
        }

        override fun onPostExecute(result: RegisterResponse) {
            progressBar.visibility = ProgressBar.GONE
            btnRegister.isEnabled = true

            Log.d("REGISTER_DEBUG", "Результат: success=${result.success}, error=${result.error}")

            if (result.success && result.employeeId != null && result.employeeName != null) {
                Log.d("REGISTER_DEBUG", "✅ Успешная регистрация! ID: ${result.employeeId}, Name: ${result.employeeName}")

                Toast.makeText(this@RegisterActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                // Сохраняем данные
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("employee_id", result.employeeId)
                    putString("employee_name", result.employeeName)
                    putBoolean("is_logged_in", true)
                    apply()
                }

                // Переходим на главный экран
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    result.error ?: "Ошибка регистрации",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("REGISTER_DEBUG", "❌ Ошибка регистрации: ${result.error}")
            }

            Log.d("REGISTER_DEBUG", "=== ЗАВЕРШЕНИЕ РЕГИСТРАЦИИ ===")
        }
    }
}