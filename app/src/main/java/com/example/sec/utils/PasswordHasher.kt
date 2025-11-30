package com.example.sec.utils

import java.security.MessageDigest

object PasswordHasher {

    fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            bytesToHex(hash)
        } catch (e: Exception) {
            throw RuntimeException("Ошибка хеширования пароля", e)
        }
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return try {
            val newHash = hashPassword(password)
            newHash.equals(hashedPassword, ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}