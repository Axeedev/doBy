package com.example.habitflow.domain.usecases.tasks

import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
){
    suspend operator fun invoke(task: Task) = repository.addTask(task)
}