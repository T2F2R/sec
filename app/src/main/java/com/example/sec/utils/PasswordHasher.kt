package com.example.sec.utils

import java.security.MessageDigest
import java.util.Base64

object PasswordHasher {

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return hashPassword(password) == hashedPassword
    }
}