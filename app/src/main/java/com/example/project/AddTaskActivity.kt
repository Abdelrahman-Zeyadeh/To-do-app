package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextDeadline: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtils.applyTheme(this)
        setContentView(R.layout.activity_add_task)

        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val spinnerTaskType = findViewById<Spinner>(R.id.spinnerTaskType)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        editTextDate = findViewById(R.id.editTextDate)
        editTextDeadline = findViewById(R.id.editTextDeadline)

        // Set up spinner with task types
        val taskTypes = arrayOf("Single Task", "Task List")
        val taskTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskTypes)
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTaskType.adapter = taskTypeAdapter

        // Set initial hint
        editTextTask.hint = "Enter task"

        // Update hint based on spinner selection
        spinnerTaskType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                editTextTask.hint = if (position == 0) {
                    "Enter the task" // Single Task
                } else {
                    "Enter list name" // Task List
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Set up spinner with priorities
        val priorities = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        // Date picker for task date
        editTextDate.setOnClickListener { showDatePickerDialog(editTextDate) }
        // Date picker for deadline
        editTextDeadline.setOnClickListener { showDatePickerDialog(editTextDeadline) }

        btnAddTask.setOnClickListener {
            val title = editTextTask.text.toString()
            val priority = spinnerPriority.selectedItem.toString()
            val date = editTextDate.text.toString()
            val deadline = editTextDeadline.text.toString()

            if (title.isNotEmpty() && priority.isNotEmpty() && date.isNotEmpty() && deadline.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("task", title)
                resultIntent.putExtra("priority", priority)
                resultIntent.putExtra("taskType", spinnerTaskType.selectedItem.toString())
                resultIntent.putExtra("date", date)
                resultIntent.putExtra("deadline", deadline)
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity
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

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            editText.setText(date)
        }, year, month, day)

        datePickerDialog.show()
    }
}