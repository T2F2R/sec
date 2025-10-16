package com.example.sec.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sec.activity.MainActivity
import com.example.sec.R
import com.example.sec.utils.PasswordHasher
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class LoginActivity : AppCompatActivity() {

    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    // Данные для подключения к PostgreSQL
    companion object {
        const val DB_URL = "jdbc:postgresql://10.0.2.2:5432/SEC"
        const val DB_USER = "postgres"
        const val DB_PASSWORD = "1234"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etLogin = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            LoginTask().execute(login, password)

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private inner class LoginTask : AsyncTask<String, Void, Boolean>() {
        private var employeeId: Int = -1
        private var employeeName: String = ""

        override fun onPreExecute() {
            progressBar.visibility = ProgressBar.VISIBLE
            btnLogin.isEnabled = false
        }

        override fun doInBackground(vararg params: String): Boolean {
            val login = params[0]
            val password = params[1]

            return try {
                Class.forName("org.postgresql.Driver")
                val connection: Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)

                val query = """
            SELECT id, last_name, first_name, patronymic, password_hash 
            FROM employees 
            WHERE login = ? AND is_admin = false
        """.trimIndent()

                val statement: PreparedStatement = connection.prepareStatement(query)
                statement.setString(1, login)

                val resultSet: ResultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val storedPasswordHash = resultSet.getString("password_hash")

                    // Проверяем пароль с хешированием
                    if (storedPasswordHash != null && PasswordHasher.verifyPassword(password, storedPasswordHash)) {
                        employeeId = resultSet.getInt("id")
                        employeeName = "${resultSet.getString("last_name")} ${resultSet.getString("first_name")}"
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(success: Boolean) {
            progressBar.visibility = ProgressBar.GONE
            btnLogin.isEnabled = true

            if (success) {
                // Сохраняем данные сотрудника
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("employee_id", employeeId)
                    putString("employee_name", employeeName)
                    apply()
                }

                // Переходим на главный экран
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}