package com.example.sec.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sec.R
import com.example.sec.adapter.MainPagerAdapter
import com.example.sec.databinding.ActivityMainBinding
import com.example.sec.fragment.HistoryFragment
import com.example.sec.fragment.ObjectsFragment
import com.example.sec.fragment.ScheduleFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("APP_DEBUG", "MainActivity onCreate")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверяем авторизацию
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val employeeId = sharedPref.getInt("employee_id", -1)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        val employeeName = sharedPref.getString("employee_name", "")

        Log.d("APP_DEBUG", "MainActivity - employeeId: $employeeId, isLoggedIn: $isLoggedIn, name: $employeeName")

        if (!isLoggedIn || employeeId == -1) {
            Log.d("APP_DEBUG", "Пользователь не авторизован, возврат в LoginActivity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Устанавливаем заголовок с именем сотрудника
        supportActionBar?.title = "Охранник: $employeeName"

        setSupportActionBar(binding.toolbar)

        initViews()
        setupViewPager()

        Log.d("APP_DEBUG", "MainActivity успешно инициализирован")
    }

    private fun initViews() {
        viewPager = binding.viewPager
        Log.d("APP_DEBUG", "Views инициализированы")
    }

    private fun setupViewPager() {
        Log.d("APP_DEBUG", "Настройка ViewPager")

        val adapter = MainPagerAdapter(this)
        adapter.addFragment(ObjectsFragment(), "Объекты")
        adapter.addFragment(ScheduleFragment(), "Расписание")
        adapter.addFragment(HistoryFragment(), "История") // <-- ДОБАВЛЕНО

        viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        Log.d("APP_DEBUG", "ViewPager настроен")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        Log.d("APP_DEBUG", "Меню создано")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                Log.d("APP_DEBUG", "Выход из системы")
                logout()
                true
            }
            R.id.action_profile -> {
                Log.d("APP_DEBUG", "Открытие профиля")
                showProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        Log.d("APP_DEBUG", "Данные очищены, переход в LoginActivity")

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProfile() {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val employeeName = sharedPref.getString("employee_name", "")
        val employeeId = sharedPref.getInt("employee_id", -1)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Профиль сотрудника")
            .setMessage("ID: $employeeId\nИмя: $employeeName")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d("APP_DEBUG", "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("APP_DEBUG", "MainActivity onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("APP_DEBUG", "MainActivity onDestroy")
    }
}
