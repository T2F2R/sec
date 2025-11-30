package com.example.sec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sec.R
import com.example.sec.classes.ScheduleItem

class ScheduleAdapter(private val items: List<ScheduleItem>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvObject: TextView = view.findViewById(R.id.tvObject)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvDate.text = item.date ?: "Дата не указана"

        holder.tvTime.text = "${item.startTime ?: "—"} — ${item.endTime ?: "—"}"

        holder.tvObject.text = item.objectName.ifEmpty { "Неизвестный объект" }

        holder.tvAddress.text = item.objectAddress.ifEmpty { "Адрес не указан" }

        holder.tvNotes.text = item.notes ?: ""
    }

    fun updateSchedule(newItems: List<ScheduleItem>) {
        (items as MutableList).clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}
