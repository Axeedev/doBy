package com.example.habitflow.domain.usecases.tasks

import com.example.habitflow.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskToCompletedUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Int) = repository.completeTask(id)
}