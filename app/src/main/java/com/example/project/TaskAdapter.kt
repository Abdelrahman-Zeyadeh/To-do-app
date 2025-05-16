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
    private val onTaskCompleted: (Task) -> Unit,
    private val onTaskLongClick: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // دالة لتحديث بيانات القائمة بدون تغيير المرجع
    fun updateTasks(newTasks: MutableList<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskText: TextView = itemView.findViewById(R.id.task_text)
        private val taskDateDeadline: TextView = itemView.findViewById(R.id.task_date_deadline)
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.task_checkbox)

        fun bind(task: Task, position: Int) {
            val emoji = when (task.priority.lowercase()) {
                "high", "مرتفعة" -> " 🚨"
                "medium", "متوسطة" -> " ⚠️"
                "low", "منخفضة" -> " 🐢"
                else -> ""
            }
            taskText.text = "${task.title} - ${task.priority}$emoji"
            taskDateDeadline.text = "Date: ${task.date}, Deadline: ${task.deadline}"

            when (task.priority.lowercase()) {
                "high", "مرتفعة" -> taskText.setTextColor(Color.RED)
                "medium", "متوسطة" -> taskText.setTextColor(Color.parseColor("#FFA500"))
                "low", "منخفضة" -> taskText.setTextColor(Color.GREEN)
                else -> taskText.setTextColor(Color.BLACK)
            }

            updateTaskTextStyle(task)

            taskCheckbox.setOnCheckedChangeListener(null)
            taskCheckbox.isChecked = task.isCompleted
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                updateTaskTextStyle(task)
                if (isChecked) onTaskCompleted(task)
            }

            itemView.setOnLongClickListener {
                onTaskLongClick(task, position)
                true
            }
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
        holder.bind(tasks[position], position)
    }

    override fun getItemCount(): Int = tasks.size
}
