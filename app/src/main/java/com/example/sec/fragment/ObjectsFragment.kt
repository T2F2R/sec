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
import com.example.sec.adapter.ObjectsAdapter
import com.example.sec.classes.GuardObject
import com.example.sec.classes.ObjectsResponse
import com.example.sec.utils.NetworkUtils
import com.google.gson.Gson

class ObjectsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: ObjectsAdapter
    private val gson = Gson()
    private var objectsList = mutableListOf<GuardObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_objects, container, false)

        initViews(view)
        setupRecyclerView()
        loadObjects()

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.objectsRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        adapter = ObjectsAdapter(objectsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadObjects() {
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

        LoadObjectsTask().execute(employeeId.toString())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }
    }

    private fun showEmpty(show: Boolean, message: String = "Объекты не найдены") {
        tvEmpty.visibility = if (show) View.VISIBLE else View.GONE
        tvEmpty.text = message
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        showEmpty(true, message)
    }

    private inner class LoadObjectsTask : AsyncTask<String, Void, ObjectsResponse>() {

        override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg params: String): ObjectsResponse {
            val employeeId = params[0]
            val response = NetworkUtils.makeGetRequest("/employee/objects?employee_id=$employeeId")

            return if (response != null) {
                try {
                    gson.fromJson(response, ObjectsResponse::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ObjectsResponse(success = false, error = "Ошибка обработки данных")
                }
            } else {
                ObjectsResponse(success = false, error = "Ошибка подключения к серверу")
            }
        }

        override fun onPostExecute(result: ObjectsResponse) {
            showLoading(false)

            if (result.success) {
                objectsList.clear()
                objectsList.addAll(result.objects)
                adapter.updateObjects(objectsList)

                if (objectsList.isEmpty()) {
                    showEmpty(true, "На вас не назначено ни одного объекта")
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
        if (objectsList.isEmpty()) {
            loadObjects()
        }
    }
}