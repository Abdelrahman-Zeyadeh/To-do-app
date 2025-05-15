package com.example.project

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Task

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCompleted: (Task) -> Unit  // Callback for task completion
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.task_text)
        val taskCheckbox: CheckBox = itemView.findViewById(R.id.task_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskText.text = "${task.title} - ${task.priority}"

        // Set text color based on priority
        when (task.priority.lowercase()) {
            "high" -> holder.taskText.setTextColor(Color.RED)
            "medium" -> holder.taskText.setTextColor(Color.parseColor("#FFA500")) // Orange
            "low" -> holder.taskText.setTextColor(Color.GREEN)
            else -> holder.taskText.setTextColor(Color.BLACK)
        }

        // Handle checkbox completion
        holder.taskCheckbox.isChecked = task.isCompleted
        holder.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            onTaskCompleted(task)  // Notify completion
        }
    }

    override fun getItemCount(): Int = tasks.size
}