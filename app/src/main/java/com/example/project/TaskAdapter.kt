package com.example.project

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Task

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCompleted: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.task_text)
        val taskDateDeadline: TextView = itemView.findViewById(R.id.task_date_deadline)
        val taskCheckbox: CheckBox = itemView.findViewById(R.id.task_checkbox)
        val subtaskLayout: LinearLayout = itemView.findViewById(R.id.subtaskContainer)


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
            taskDateDeadline.text = "Date: ${task.date}, Deadline: ${task.deadline}"
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
        val task = tasks[position]   // Get the task at the current position

        holder.bind(task)   // Bind common data to views (including priority, checkbox etc)

        // Then handle subtasks and visibility:
        if (task.taskType == "Task List" && task.subtasks.isNotEmpty()) {
            holder.subtaskLayout.visibility = View.VISIBLE
            holder.subtaskLayout.removeAllViews()
            for (sub in task.subtasks) {
                val subtaskView = TextView(holder.itemView.context)
                subtaskView.text = "• $sub"
                subtaskView.setPadding(16, 4, 0, 4)
                subtaskView.setTextColor(Color.DKGRAY)
                holder.subtaskLayout.addView(subtaskView)
            }
        } else {
            holder.subtaskLayout.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int = tasks.size
}