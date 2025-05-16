package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var spinnerTaskType: Spinner
    private lateinit var spinnerPriority: Spinner
    private lateinit var editTextDate: EditText
    private lateinit var editTextDeadline: EditText
    private lateinit var btnAddTask: Button

    private var isEditMode = false
    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // ربط المتغيرات بالعناصر من layout
        editTextTask = findViewById(R.id.editTextTask)
        spinnerTaskType = findViewById(R.id.spinnerTaskType)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        editTextDate = findViewById(R.id.editTextDate)
        editTextDeadline = findViewById(R.id.editTextDeadline)
        btnAddTask = findViewById(R.id.btnAddTask)

        // تهيئة Spinner نوع المهمة
        val taskTypes = arrayOf("Single Task", "Task List")
        val taskTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskTypes)
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTaskType.adapter = taskTypeAdapter

        // تغيير التلميح حسب نوع المهمة المختار
        spinnerTaskType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editTextTask.hint = if (position == 0) "أدخل المهمة" else "أدخل اسم القائمة"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // تهيئة Spinner الأولوية
        val priorities = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        // ضبط DatePicker لكل من تاريخ المهمة و الموعد النهائي
        editTextDate.setOnClickListener { showDatePickerDialog(editTextDate) }
        editTextDeadline.setOnClickListener { showDatePickerDialog(editTextDeadline) }

        // التحقق إن كانت مهمة للتعديل أم إضافة جديدة
        val intent = intent
        if (intent.hasExtra("task")) {
            isEditMode = true
            position = intent.getIntExtra("position", -1)
            val title = intent.getStringExtra("task")
            val priority = intent.getStringExtra("priority")
            val taskType = intent.getStringExtra("taskType")
            val date = intent.getStringExtra("date")
            val deadline = intent.getStringExtra("deadline")

            // تعبئة الحقول بالبيانات القديمة
            editTextTask.setText(title)
            editTextDate.setText(date)
            editTextDeadline.setText(deadline)

            // ضبط spinner الأولوية حسب المهمة
            val priorityIndex = priorities.indexOf(priority ?: "Medium")
            if (priorityIndex >= 0) spinnerPriority.setSelection(priorityIndex)

            // ضبط spinner نوع المهمة
            val taskTypeIndex = taskTypes.indexOf(taskType ?: "Single Task")
            if (taskTypeIndex >= 0) spinnerTaskType.setSelection(taskTypeIndex)
        }

        // تغيير نص الزر حسب الوضع
        btnAddTask.text = if (isEditMode) "تعديل المهمة" else "إضافة مهمة"

        // عند الضغط على الزر، تحقق وأرسل البيانات
        btnAddTask.setOnClickListener {
            val title = editTextTask.text.toString().trim()
            val priority = spinnerPriority.selectedItem.toString()
            val date = editTextDate.text.toString().trim()
            val deadline = editTextDeadline.text.toString().trim()
            val taskType = spinnerTaskType.selectedItem.toString()

            if (title.isNotEmpty() && priority.isNotEmpty() && date.isNotEmpty() && deadline.isNotEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("task", title)
                    putExtra("priority", priority)
                    putExtra("taskType", taskType)
                    putExtra("date", date)
                    putExtra("deadline", deadline)
                    if (isEditMode) putExtra("position", position)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            editText.setText(date)
        }, year, month, day).show()
    }
}
