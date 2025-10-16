package com.example.sec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sec.R
import com.example.sec.classes.ScheduleItem
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(
    private var scheduleList: List<ScheduleItem> = emptyList()
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvObjectName: TextView = itemView.findViewById(R.id.tvObjectName)
        private val tvObjectAddress: TextView = itemView.findViewById(R.id.tvObjectAddress)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        fun bind(scheduleItem: ScheduleItem) {
            // Форматируем дату
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = try {
                inputFormat.parse(scheduleItem.date)
            } catch (e: Exception) {
                null
            }

            val formattedDate = date?.let { outputFormat.format(it) } ?: scheduleItem.date
            tvDate.text = formattedDate

            // Время
            tvTime.text = "${scheduleItem.startTime} - ${scheduleItem.endTime}"

            // Объект
            tvObjectName.text = scheduleItem.objectName
            tvObjectAddress.text = scheduleItem.objectAddress

            // Заметки
            tvNotes.text = scheduleItem.notes.ifEmpty { "Дополнительные заметки отсутствуют" }

            // Подсветка сегодняшней даты
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val itemDate = inputFormat.parse(scheduleItem.date)
            if (itemDate != null && itemDate == today) {
                itemView.setBackgroundColor(itemView.context.getColor(R.color.colorTodayBackground))
            } else {
                itemView.setBackgroundColor(itemView.context.getColor(android.R.color.transparent))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateSchedule(newSchedule: List<ScheduleItem>) {
        scheduleList = newSchedule.sortedBy { it.date }
        notifyDataSetChanged()
    }
}