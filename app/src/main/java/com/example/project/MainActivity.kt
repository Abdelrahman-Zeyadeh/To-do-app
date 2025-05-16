package com.example.project

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCompletedTasks: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var selectedDateText: TextView

    private val tasks = mutableListOf<Task>()
    private val completedTasks = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var completedTaskAdapter: TaskAdapter
    private var selectedDate: String = ""

    companion object {
        const val REQUEST_CODE_ADD_TASK = 100
        const val REQUEST_CODE_EDIT_TASK = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Task List"

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerViewCompletedTasks = findViewById(R.id.recyclerViewCompletedTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        bottomNav = findViewById(R.id.bottom_nav)
        selectedDateText = findViewById(R.id.selectedDateText)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {}
                R.id.nav_settings -> {}
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // إنشاء الـ adapters مرة واحدة فقط
        taskAdapter = TaskAdapter(tasks,
            onTaskCompleted = { task -> onTaskCompleted(task) },
            onTaskLongClick = { task, position -> showTaskOptionsDialog(task, position, isCompleted = false) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        completedTaskAdapter = TaskAdapter(completedTasks,
            onTaskCompleted = {},
            onTaskLongClick = { task, position -> showTaskOptionsDialog(task, position, isCompleted = true) }
        )
        recyclerViewCompletedTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewCompletedTasks.adapter = completedTaskAdapter

        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TASK)
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedDate = ""
                    selectedDateText.text = "All Tasks"
                    filterTasksByDate()
                    true
                }
                R.id.nav_calendar -> {
                    showDatePickerDialog()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }

        selectedDateText.text = "All Tasks"
        filterTasksByDate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val title = data.getStringExtra("task")
            val priority = data.getStringExtra("priority")
            val date = data.getStringExtra("date") ?: ""
            val deadline = data.getStringExtra("deadline") ?: ""

            if (!title.isNullOrEmpty() && !priority.isNullOrEmpty()) {
                if (requestCode == REQUEST_CODE_ADD_TASK) {
                    val newTask = Task(title, priority, date = date, deadline = deadline)
                    tasks.add(newTask)
                    filterTasksByDate()
                    recyclerView.scrollToPosition(tasks.size - 1)
                } else if (requestCode == REQUEST_CODE_EDIT_TASK) {
                    val position = data.getIntExtra("position", -1)
                    if (position != -1 && position < tasks.size) {
                        val editedTask = Task(title, priority, date = date, deadline = deadline)
                        tasks[position] = editedTask
                        filterTasksByDate()
                        taskAdapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun onTaskCompleted(task: Task) {
        tasks.remove(task)
        completedTasks.add(task)
        filterTasksByDate()

        if (completedTasks.isNotEmpty()) {
            recyclerViewCompletedTasks.visibility = View.VISIBLE
            findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.VISIBLE
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            selectedDateText.text = "Tasks for: $selectedDate"
            filterTasksByDate()
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun filterTasksByDate() {
        val filteredTasks = if (selectedDate.isEmpty()) {
            tasks.toMutableList()
        } else {
            tasks.filter { it.date == selectedDate || it.deadline == selectedDate }.toMutableList()
        }

        val filteredCompletedTasks = if (selectedDate.isEmpty()) {
            completedTasks.toMutableList()
        } else {
            completedTasks.filter { it.date == selectedDate || it.deadline == selectedDate }.toMutableList()
        }

        taskAdapter.updateTasks(filteredTasks)
        completedTaskAdapter.updateTasks(filteredCompletedTasks)

        if (filteredCompletedTasks.isNotEmpty()) {
            recyclerViewCompletedTasks.visibility = View.VISIBLE
            findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.VISIBLE
        } else {
            recyclerViewCompletedTasks.visibility = View.GONE
            findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.GONE
        }
    }

    private fun showTaskOptionsDialog(task: Task, position: Int, isCompleted: Boolean) {
        val options = arrayOf("تعديل", "حذف")

        AlertDialog.Builder(this)
            .setTitle("اختر إجراء")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (!isCompleted) {
                            val intent = Intent(this, AddTaskActivity::class.java).apply {
                                putExtra("task", task.title)
                                putExtra("priority", task.priority)
                                putExtra("date", task.date)
                                putExtra("deadline", task.deadline)
                                putExtra("taskType", "Single Task")
                                putExtra("position", tasks.indexOf(task)) // الموقع في القائمة الأصلية
                            }
                            startActivityForResult(intent, REQUEST_CODE_EDIT_TASK)
                        } else {
                            Toast.makeText(this, "تعديل المهام المكتملة غير مفعّل", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {
                        if (isCompleted) {
                            val originalIndex = completedTasks.indexOf(task)
                            if (originalIndex != -1) {
                                completedTasks.removeAt(originalIndex)
                                completedTaskAdapter.notifyItemRemoved(originalIndex)
                                if (completedTasks.isEmpty()) {
                                    recyclerViewCompletedTasks.visibility = View.GONE
                                    findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.GONE
                                }
                            }
                        } else {
                            val originalIndex = tasks.indexOf(task)
                            if (originalIndex != -1) {
                                tasks.removeAt(originalIndex)
                                taskAdapter.notifyItemRemoved(originalIndex)
                            }
                        }
                        Toast.makeText(this, "تم حذف المهمة", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
}
