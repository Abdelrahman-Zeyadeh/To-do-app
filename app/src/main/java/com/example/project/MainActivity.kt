package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Task
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCompletedTasks: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton

    private val tasks = mutableListOf<Task>()
    private val completedTasks = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var completedTaskAdapter: TaskAdapter

    companion object {
        const val REQUEST_CODE_ADD_TASK = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "قائمة المهام"

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerViewCompletedTasks = findViewById(R.id.recyclerViewCompletedTasks)
        fabAddTask = findViewById(R.id.fabAddTask)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> { /* Do something */ }
                R.id.nav_settings -> { /* Do something */ }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
            val title = data?.getStringExtra("task")
            val priority = data?.getStringExtra("priority")
            if (!title.isNullOrEmpty() && !priority.isNullOrEmpty()) {
                val newTask = Task(title, priority)
                tasks.add(newTask)
                taskAdapter.notifyItemInserted(tasks.size - 1)
                recyclerView.scrollToPosition(tasks.size - 1)
            }
        }
    }

    private fun onTaskCompleted(task: Task) {
        tasks.remove(task)
        completedTasks.add(task)
        taskAdapter.notifyDataSetChanged()
        completedTaskAdapter.notifyItemInserted(completedTasks.size - 1)

        // Show the completed tasks recycler view if there are completed tasks
        if (completedTasks.isNotEmpty()) {
            recyclerViewCompletedTasks.visibility = View.VISIBLE
            findViewById<TextView>(R.id.CompletedTasksTitle).visibility = View.VISIBLE
        }
    }

}