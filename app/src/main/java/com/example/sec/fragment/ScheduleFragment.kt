package com.example.sec.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sec.R
import com.example.sec.adapter.ScheduleAdapter
import com.example.sec.classes.ScheduleItem
import com.example.sec.classes.ScheduleResponse
import com.example.sec.utils.NetworkUtils
import com.google.gson.Gson

class ScheduleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: ScheduleAdapter
    private val gson = Gson()
    private var scheduleList = mutableListOf<ScheduleItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        initViews(view)
        setupRecyclerView()
        loadSchedule()

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.scheduleRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        adapter = ScheduleAdapter(scheduleList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadSchedule() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val employeeId = sharedPref.getInt("employee_id", -1)

        if (employeeId == -1) {
            showError("Ошибка авторизации")
            return
        }

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showError("Нет подключения к интернету")
            return
        }

        LoadScheduleTask().execute(employeeId.toString())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }
    }

    private fun showEmpty(show: Boolean, message: String = "Расписание отсутствует") {
        tvEmpty.visibility = if (show) View.VISIBLE else View.GONE
        tvEmpty.text = message
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        showEmpty(true, message)
    }

    private inner class LoadScheduleTask : AsyncTask<String, Void, ScheduleResponse>() {

        override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg params: String): ScheduleResponse {
            val employeeId = params[0]
            val response = NetworkUtils.makeGetRequest("/employee/schedule?employee_id=$employeeId")

            return if (response != null) {
                try {
                    gson.fromJson(response, ScheduleResponse::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ScheduleResponse(success = false, error = "Ошибка обработки данных")
                }
            } else {
                ScheduleResponse(success = false, error = "Ошибка подключения к серверу")
            }
        }

        override fun onPostExecute(result: ScheduleResponse) {
            showLoading(false)

            if (result.success) {
                scheduleList.clear()
                scheduleList.addAll(result.schedule)
                adapter.updateSchedule(scheduleList)

                if (scheduleList.isEmpty()) {
                    showEmpty(true, "На ближайшее время расписание отсутствует")
                } else {
                    showEmpty(false)
                }
            } else {
                showError(result.error ?: "Неизвестная ошибка")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем данные при возвращении на фрагмент
        if (scheduleList.isEmpty()) {
            loadSchedule()
        }
    }
}