package com.example.habitflow.domain.usecases.tasks

import com.example.habitflow.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Int) = repository.deleteTask(taskId)
}