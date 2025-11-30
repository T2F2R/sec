package com.example.sec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sec.R
import com.example.sec.classes.GuardObject

class ObjectsAdapter(
    private var objectsList: List<GuardObject> = emptyList()
) : RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder>() {

    // Интерфейс для обработки кликов по объекту
    interface OnObjectClickListener {
        fun onObjectClick(guardObject: GuardObject)
        fun onShowOnMapClick(guardObject: GuardObject)
    }

    private var onObjectClickListener: OnObjectClickListener? = null

    // Устанавливаем слушатель кликов
    fun setOnObjectClickListener(listener: OnObjectClickListener) {
        this.onObjectClickListener = listener
    }

    inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvObjectName: TextView = itemView.findViewById(R.id.tvObjectName)
        private val tvObjectAddress: TextView = itemView.findViewById(R.id.tvObjectAddress)
        private val tvObjectDescription: TextView = itemView.findViewById(R.id.tvObjectDescription)
        private val tvCoordinates: TextView = itemView.findViewById(R.id.tvCoordinates)
        private val btnShowOnMap: Button = itemView.findViewById(R.id.btnShowOnMap)

        fun bind(guardObject: GuardObject) {
            tvObjectName.text = guardObject.name
            tvObjectAddress.text = guardObject.address
            tvObjectDescription.text = guardObject.description.ifEmpty { "Описание отсутствует" }

            val coordinates = if (guardObject.hasValidCoordinates()) {
                "Ш: ${"%.6f".format(guardObject.latitude)}, Д: ${"%.6f".format(guardObject.longitude)}"
            } else {
                "Координаты не указаны"
            }
            tvCoordinates.text = coordinates

            // Обработчик клика по всей карточке объекта
            itemView.setOnClickListener {
                onObjectClickListener?.onObjectClick(guardObject)
            }

            // Обработчик для кнопки показа на карте
            if (guardObject.hasValidCoordinates()) {
                btnShowOnMap.visibility = View.VISIBLE
                btnShowOnMap.setOnClickListener {
                    onObjectClickListener?.onShowOnMapClick(guardObject)
                }
            } else {
                btnShowOnMap.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_object, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        holder.bind(objectsList[position])
    }

    override fun getItemCount(): Int = objectsList.size

    fun updateObjects(newObjects: List<GuardObject>) {
        objectsList = newObjects
        notifyDataSetChanged()
    }
}