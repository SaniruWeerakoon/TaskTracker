package com.example.tasktracker.utils

data class TaskData(
    val taskId: String,
    val task: String,
    var complete: Boolean = false,
    val description: String = ""
) {

}
