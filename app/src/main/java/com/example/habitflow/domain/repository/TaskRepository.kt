package com.example.habitflow.domain.repository

import com.example.habitflow.domain.entities.tasks.CompletedTask
import com.example.habitflow.domain.entities.tasks.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getCompletedTasks() : Flow<List<CompletedTask>>

    fun getTasks() : Flow<List<Task>>

    suspend fun addTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    suspend fun updateTask(task: Task)

    suspend fun completeTask(taskId: Int)

    suspend fun returnTask(taskId: Int)

    fun getNumberOfCompletedTasks() : Flow<Int>


}