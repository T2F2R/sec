package com.example.sec.config

object DatabaseConfig {
    // Для эмулятора Android
    const val DB_URL = "jdbc:postgresql://10.0.2.2:5432/SEC"
    const val DB_USER = "postgres"
    const val DB_PASSWORD = "1234"

    const val DB_URL_WITH_PARAMS = "$DB_URL?binaryTransfer=true&reWriteBatchedInserts=true"
}