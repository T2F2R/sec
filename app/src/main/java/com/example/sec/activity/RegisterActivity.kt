package com.example.sec.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sec.R
import com.example.sec.utils.PasswordHasher
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

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

    private inner class RegisterTask : AsyncTask<String, Void, RegisterResult>() {

        override fun onPreExecute() {
            progressBar.visibility = ProgressBar.VISIBLE
            btnRegister.isEnabled = false
        }

        override fun doInBackground(vararg params: String): RegisterResult {
            val lastName = params[0]
            val firstName = params[1]
            val patronymic = params[2]
            val passportSeries = params[3].toInt()
            val passportNumber = params[4].toInt()
            val login = params[5]
            val password = params[6]

            return try {
                Class.forName("org.postgresql.Driver")
                val connection: Connection = DriverManager.getConnection(
                    LoginActivity.DB_URL,
                    LoginActivity.DB_USER,
                    LoginActivity.DB_PASSWORD
                )

                // 1. Проверяем существование сотрудника по ФИО и паспорту
                val checkQuery = """
                    SELECT id FROM employees 
                    WHERE last_name = ? AND first_name = ? 
                    AND passport_series = ? AND passport_number = ?
                    AND password_hash IS NULL
                """.trimIndent()

                val checkStatement: PreparedStatement = connection.prepareStatement(checkQuery)
                checkStatement.setString(1, lastName)
                checkStatement.setString(2, firstName)
                checkStatement.setInt(3, passportSeries)
                checkStatement.setInt(4, passportNumber)

                val resultSet = checkStatement.executeQuery()

                if (!resultSet.next()) {
                    return RegisterResult(success = false, error = "Сотрудник не найден или уже зарегистрирован")
                }

                val employeeId = resultSet.getInt("id")

                // 2. Проверяем уникальность логина
                val loginCheckQuery = "SELECT id FROM employees WHERE login = ?"
                val loginCheckStatement: PreparedStatement = connection.prepareStatement(loginCheckQuery)
                loginCheckStatement.setString(1, login)

                val loginResultSet = loginCheckStatement.executeQuery()
                if (loginResultSet.next()) {
                    return RegisterResult(success = false, error = "Логин уже занят")
                }

                // 3. Хешируем пароль и обновляем запись
                val hashedPassword = PasswordHasher.hashPassword(password)

                val updateQuery = """
                    UPDATE employees 
                    SET login = ?, password_hash = ? 
                    WHERE id = ?
                """.trimIndent()

                val updateStatement: PreparedStatement = connection.prepareStatement(updateQuery)
                updateStatement.setString(1, login)
                updateStatement.setString(2, hashedPassword)
                updateStatement.setInt(3, employeeId)

                val rowsAffected = updateStatement.executeUpdate()

                if (rowsAffected > 0) {
                    RegisterResult(success = true, employeeId = employeeId)
                } else {
                    RegisterResult(success = false, error = "Ошибка при регистрации")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                RegisterResult(success = false, error = "Ошибка подключения: ${e.message}")
            }
        }

        override fun onPostExecute(result: RegisterResult) {
            progressBar.visibility = ProgressBar.GONE
            btnRegister.isEnabled = true

            if (result.success) {
                Toast.makeText(this@RegisterActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                // Сохраняем данные и переходим на главный экран
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("employee_id", result.employeeId!!)
                    putString("employee_name", "${etLastName.text} ${etFirstName.text}")
                    apply()
                }

                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@RegisterActivity, result.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    data class RegisterResult(
        val success: Boolean,
        val employeeId: Int? = null,
        val error: String? = null
    )
}