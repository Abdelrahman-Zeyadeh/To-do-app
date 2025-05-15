package com.example.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val etTask = findViewById<EditText>(R.id.etTask)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val spinner = findViewById<Spinner>(R.id.spinnerPriority)

        // Setup priorities
        val priorities = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinner.adapter = adapter

        btnSave.setOnClickListener {
            val task = etTask.text.toString()
            val priority = spinner.selectedItem.toString()

            if (task.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("task", task)
                resultIntent.putExtra("priority", priority)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                etTask.error = "Please enter a task"
            }
        }
    }
}