package com.example.habitflow.domain.usecases.tasks

import com.example.habitflow.domain.entities.tasks.Task
import com.example.habitflow.domain.repository.TaskRepository
import javax.inject.Inject

class EditTaskUseCase @Inject constructor(
    private val repository: TaskRepository
){
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}