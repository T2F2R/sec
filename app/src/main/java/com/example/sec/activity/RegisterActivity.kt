package com.example.sec.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sec.R

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


            val lastName = params[0]
            val firstName = params[1]
            val patronymic = params[2]
            val login = params[5]
            val password = params[6]

            }

            }




                } catch (e: Exception) {
                }
            } else {
            }
        }
    }
}