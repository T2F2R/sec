package com.example.sec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sec.R
import com.example.sec.classes.GuardObject

class ObjectsAdapter(
    private var objectsList: List<GuardObject> = emptyList()
) : RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder>() {

    inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvObjectName: TextView = itemView.findViewById(R.id.tvObjectName)
        private val tvObjectAddress: TextView = itemView.findViewById(R.id.tvObjectAddress)
        private val tvObjectDescription: TextView = itemView.findViewById(R.id.tvObjectDescription)
        private val tvCoordinates: TextView = itemView.findViewById(R.id.tvCoordinates)

        fun bind(guardObject: GuardObject) {
            tvObjectName.text = guardObject.name
            tvObjectAddress.text = guardObject.address
            tvObjectDescription.text = guardObject.description.ifEmpty { "Описание отсутствует" }

            val coordinates = if (guardObject.latitude != 0.0 && guardObject.longitude != 0.0) {
                "Ш: ${"%.6f".format(guardObject.latitude)}, Д: ${"%.6f".format(guardObject.longitude)}"
            } else {
                "Координаты не указаны"
            }
            tvCoordinates.text = coordinates
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