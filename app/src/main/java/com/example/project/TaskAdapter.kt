package com.example.project

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Task

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCompleted: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.task_text)
        val taskCheckbox: CheckBox = itemView.findViewById(R.id.task_checkbox)

        init {
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                val task = tasks[adapterPosition]
                task.isCompleted = isChecked
                updateTaskTextStyle(task)

                if (isChecked) {
                    onTaskCompleted(task)
                }
            }
        }

        fun bind(task: Task) {
            val emoji = when (task.priority.lowercase()) {
                "high" -> " 🚨"
                "medium" -> " ⚠️"
                "low" -> " 🐢"
                else -> ""
            }
            taskText.text = "${task.title} - ${task.priority}$emoji"
            taskCheckbox.isChecked = task.isCompleted

            // Set text color based on priority
            when (task.priority.lowercase()) {
                "high" -> taskText.setTextColor(Color.RED)
                "medium" -> taskText.setTextColor(Color.parseColor("#FFA500")) // Orange
                "low" -> taskText.setTextColor(Color.GREEN)
                else -> taskText.setTextColor(Color.BLACK)
            }

            updateTaskTextStyle(task)
        }

        private fun updateTaskTextStyle(task: Task) {
            if (task.isCompleted) {
                taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                taskText.paintFlags = taskText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = tasks.size
}