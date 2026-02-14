package com.example.habitflow.domain.usecases.tasks

import com.example.habitflow.domain.repository.TaskRepository
import javax.inject.Inject

class GetNumberOfCompletedTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke() = repository.getNumberOfCompletedTasks()
}