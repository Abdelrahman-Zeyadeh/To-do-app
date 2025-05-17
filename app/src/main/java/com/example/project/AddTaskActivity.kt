package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextDeadline: EditText
    private lateinit var subtaskLayout: LinearLayout
    private lateinit var editTextSubtask: EditText
    private lateinit var recyclerViewSubtasks: RecyclerView
    private lateinit var btnAddSubtask: Button

    private val subtaskList = mutableListOf<String>()
    private lateinit var subtaskAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val btnAddTask: Button = findViewById(R.id.btnAddTask)
        val editTextTask: EditText = findViewById(R.id.editTextTask)
        val spinnerTaskType: Spinner = findViewById(R.id.spinnerTaskType)
        val spinnerPriority: Spinner = findViewById(R.id.spinnerPriority)
        editTextDate = findViewById(R.id.editTextDate)
        editTextDeadline = findViewById(R.id.editTextDeadline)

        // Subtask UI
        subtaskLayout = findViewById(R.id.subtaskLayout)
        editTextSubtask = findViewById(R.id.editTextSubtask)
        btnAddSubtask = findViewById(R.id.btnAddSubtask)
        recyclerViewSubtasks = findViewById(R.id.recyclerViewSubtasks)

        // Set up subtask RecyclerView
        subtaskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, subtaskList)
        recyclerViewSubtasks.layoutManager = LinearLayoutManager(this)
        recyclerViewSubtasks.adapter = object : RecyclerView.Adapter<SubtaskViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
                val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                return SubtaskViewHolder(view)
            }

            override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
                holder.bind(subtaskList[position])
            }

            override fun getItemCount(): Int = subtaskList.size
        }

        // Spinner for task type
        val taskTypes = arrayOf("Single Task", "Task List")
        val taskTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskTypes)
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTaskType.adapter = taskTypeAdapter

        // Spinner for priority
        val priorities = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        // Date pickers
        editTextDate.setOnClickListener { showDatePickerDialog(editTextDate) }
        editTextDeadline.setOnClickListener { showDatePickerDialog(editTextDeadline) }

        // Show/hide subtask section based on task type
        spinnerTaskType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                if (position == 1) { // Task List
                    subtaskLayout.visibility = View.VISIBLE
                    editTextTask.hint = "Enter list name"
                } else {
                    subtaskLayout.visibility = View.GONE
                    editTextTask.hint = "Enter the task"
                    subtaskList.clear()
                    recyclerViewSubtasks.adapter?.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Add subtask button
        btnAddSubtask.setOnClickListener {
            val subtask = editTextSubtask.text.toString().trim()
            if (subtask.isNotEmpty()) {
                subtaskList.add(subtask)
                recyclerViewSubtasks.adapter?.notifyItemInserted(subtaskList.size - 1)
                editTextSubtask.text.clear()
            } else {
                Toast.makeText(this, "Please enter a subtask", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button
        btnAddTask.setOnClickListener {
            val title = editTextTask.text.toString()
            val priority = spinnerPriority.selectedItem.toString()
            val date = editTextDate.text.toString()
            val deadline = editTextDeadline.text.toString()
            val taskType = spinnerTaskType.selectedItem.toString()

            if (title.isNotEmpty() && priority.isNotEmpty() && date.isNotEmpty() && deadline.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("task", title)
                resultIntent.putExtra("priority", priority)
                resultIntent.putExtra("taskType", taskType)
                resultIntent.putExtra("date", date)
                resultIntent.putExtra("deadline", deadline)
                resultIntent.putStringArrayListExtra("subtasks", ArrayList(subtaskList))
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                editText.setText(date)
            }, year, month, day)

        datePickerDialog.show()
    }

    class SubtaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(subtask: String) {
            (itemView as TextView).text = subtask
        }
    }
}
