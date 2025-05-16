package com.example

data class Task(
    val title: String,
    val priority: String,
    var isCompleted: Boolean = false,
    val date: String = "",
    val deadline: String = ""
)
