package com.example.sec.activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.sec.R
import com.example.sec.classes.GuardObject

class MapActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var guardObject: GuardObject
    private lateinit var tvObjectName: TextView
    private lateinit var tvObjectAddress: TextView
    private lateinit var tvCoordinates: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Получаем переданный объект
        guardObject = intent.getSerializableExtra("GUARD_OBJECT") as GuardObject

        initViews()
        setupWebView()
        setupBackPressedHandler()
    }

    private fun initViews() {
        webView = findViewById(R.id.webView)  // Теперь webView будет найден
        tvObjectName = findViewById(R.id.tvObjectName)
        tvObjectAddress = findViewById(R.id.tvObjectAddress)
        tvCoordinates = findViewById(R.id.tvCoordinates)

        // Заполняем данные об объекте
        tvObjectName.text = guardObject.name
        tvObjectAddress.text = guardObject.address

        val coordinates = "Ш: ${"%.6f".format(guardObject.latitude)}, Д: ${"%.6f".format(guardObject.longitude)}"
        tvCoordinates.text = coordinates
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()

        if (guardObject.hasValidCoordinates()) {
            // Загружаем Яндекс Карты с маркером
            val yandexMapsUrl = "https://yandex.ru/maps/?pt=${guardObject.longitude},${guardObject.latitude}&z=16&l=map"
            webView.loadUrl(yandexMapsUrl)
        } else {
            // Если координаты невалидны, показываем сообщение
            val htmlContent = """
                <html>
                    <body style="display:flex; justify-content:center; align-items:center; height:100vh; margin:0;">
                        <h2 style="color:red; text-align:center;">Координаты объекта не указаны</h2>
                    </body>
                </html>
            """.trimIndent()
            webView.loadData(htmlContent, "text/html", "UTF-8")
        }
    }

    private fun setupBackPressedHandler() {
        // Современный способ обработки кнопки "Назад"
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    // Если нельзя вернуться назад в WebView, закрываем активность
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}