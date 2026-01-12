package com.example.habitflow.domain.usecases.tasks

import com.example.habitflow.domain.entities.Task
import com.example.habitflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = repository.getTasks()
}