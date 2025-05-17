package com.example.com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubtaskAdapter(private val subtasks: List<String>) :
    RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder>() {

    inner class SubtaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subtaskText: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SubtaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.subtaskText.text = subtasks[position]
    }

    override fun getItemCount(): Int = subtasks.size
}
