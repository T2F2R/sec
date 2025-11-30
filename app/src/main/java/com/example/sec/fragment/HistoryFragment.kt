package com.example.sec.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
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

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: ScheduleAdapter
    private val gson = Gson()
    private var historyList = mutableListOf<ScheduleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        initViews(view)
        setupRecyclerView()
        loadHistory()

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.historyRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        adapter = ScheduleAdapter(historyList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadHistory() {
        val sharedPref = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val employeeId = sharedPref.getInt("employee_id", -1)

        if (employeeId == -1) {
            showError("Ошибка авторизации")
            return
        }

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showError("Нет подключения к интернету")
            return
        }

        LoadHistoryTask().execute(employeeId.toString())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        if (show) tvEmpty.visibility = View.GONE
    }

    private fun showError(message: String) {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        tvEmpty.text = message
        tvEmpty.visibility = View.VISIBLE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private inner class LoadHistoryTask : AsyncTask<String, Void, ScheduleResponse>() {

        override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg params: String): ScheduleResponse {
            val employeeId = params[0]

            val response = NetworkUtils.makeGetRequest(
                "/employee/history?employee_id=$employeeId"
            )

            Log.d("HISTORY_DEBUG", "Response from server: $response")

            return if (response != null) {
                try {
                    gson.fromJson(response, ScheduleResponse::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ScheduleResponse(success = false, error = "Ошибка обработки данных: ${e.message}")
                }
            } else {
                ScheduleResponse(success = false, error = "Ошибка подключения к серверу")
            }
        }

        override fun onPostExecute(result: ScheduleResponse) {
            showLoading(false)

            Log.d("HISTORY_DEBUG", "Result success: ${result.success}")
            Log.d("HISTORY_DEBUG", "Result history: ${result.history}")
            Log.d("HISTORY_DEBUG", "Result schedule: ${result.schedule}")
            Log.d("HISTORY_DEBUG", "Result error: ${result.error}")

            if (result.success) {
                historyList.clear()

                // Пробуем оба варианта: history и schedule
                val dataToAdd = result.history ?: result.schedule

                if (dataToAdd != null) {
                    historyList.addAll(dataToAdd)
                    adapter.notifyDataSetChanged()

                    if (historyList.isEmpty()) {
                        showError("История смен пуста")
                    } else {
                        tvEmpty.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        Log.d("HISTORY_DEBUG", "Loaded ${historyList.size} items")
                    }
                } else {
                    showError("Данные не получены (null)")
                    Log.e("HISTORY_DEBUG", "Both history and schedule are null")
                }
            } else {
                showError(result.error ?: "Ошибка загрузки истории")
            }
        }
    }
}