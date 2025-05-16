package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
                R.id.nav_home -> {
                    // Handle home action
                }
                R.id.nav_settings -> {
                    // Handle settings action
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Initialize adapters
        taskAdapter = TaskAdapter(tasks) { task -> onTaskCompleted(task) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        completedTaskAdapter = TaskAdapter(completedTasks) { /* No action needed */ }
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
                R.id.nav_settings -> {
                    // Handle settings action
                    true
                }
                else -> false
            }
        }

        // Initialize with all tasks
        selectedDateText.text = "All Tasks"
        filterTasksByDate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
            val title = data?.getStringExtra("task")
            val priority = data?.getStringExtra("priority")
            val date = data?.getStringExtra("date") ?: ""
            val deadline = data?.getStringExtra("deadline") ?: ""

            // Ensure title and priority are not null or empty
            if (!title.isNullOrEmpty() && !priority.isNullOrEmpty()) {
                val newTask = Task(title, priority, date = date, deadline = deadline)
                tasks.add(newTask)
                filterTasksByDate()
                recyclerView.scrollToPosition(tasks.size - 1)
            }
        }
    }

    private fun onTaskCompleted(task: Task) {
        tasks.remove(task)
        completedTasks.add(task)
        filterTasksByDate()

        // Show the completed tasks recycler view if there are completed tasks
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

        taskAdapter = TaskAdapter(filteredTasks) { task -> onTaskCompleted(task) }
        recyclerView.adapter = taskAdapter

        completedTaskAdapter = TaskAdapter(filteredCompletedTasks) { /* No action needed */ }
        recyclerViewCompletedTasks.adapter = completedTaskAdapter

        // Update visibility of completed tasks section
        if (filteredCompletedTasks.isNotEmpty()) {
            recyclerViewCompletedTasks.visibility = View.VISIBLE
            findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.VISIBLE
        } else {
            recyclerViewCompletedTasks.visibility = View.GONE
            findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.GONE
        }
    }
}