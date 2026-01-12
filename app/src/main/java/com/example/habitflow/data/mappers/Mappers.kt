package com.example.habitflow.data.mappers

import com.example.habitflow.data.local.tasks.TaskEntity
import com.example.habitflow.domain.entities.Task

fun Task.toTaskEntity(): TaskEntity{
    return TaskEntity(
        id = id ,
        title = title ,
        date = date ,
        note = note ,
        isCompleted = isCompleted
    )
}
fun TaskEntity.toTask(): Task{
    return Task(
        id = id ,
        title = title ,
        date = date ,
        note = note ,
        isCompleted = isCompleted
    )
}