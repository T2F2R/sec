package com.example.sec.utils

import java.security.MessageDigest

object PasswordHasher {

    fun hashPassword(password: String): String {
                val digest = MessageDigest.getInstance("SHA-256")
                val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
    }
}