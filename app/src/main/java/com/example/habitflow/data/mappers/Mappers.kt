package com.example.habitflow.data.mappers

import com.example.habitflow.data.local.goals.GoalEntity
import com.example.habitflow.data.local.goals.GoalWithMilestoneEntity
import com.example.habitflow.data.local.goals.MilestoneEntity
import com.example.habitflow.data.local.tasks.TaskEntity
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.entities.GoalCategory
import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.domain.entities.Priority
import com.example.habitflow.domain.entities.Task

fun Task.toTaskEntity(
    taskId: Int
): TaskEntity{
    return TaskEntity(
        id = taskId ,
        title = title ,
        deadlineMillis = deadlineMillis ,
        note = note ,
        isCompleted = isCompleted,
        category = category.title,
        priority = priority.name
    )
}
fun TaskEntity.toTask(): Task{
    return Task(
        id = id ,
        title = title ,
        deadlineMillis = deadlineMillis,
        note = note,
        category = GoalCategory.valueOf(this.category.uppercase()),
        priority = Priority.valueOf(this.priority.uppercase()),
        isCompleted = isCompleted
    )
}

fun MilestoneEntity.toMilestone() : Milestone{
    return Milestone(
        id,
        title,
        isCompleted
    )
}

fun Goal.toGoalEntity(coverUri: String? = null) : GoalEntity{
    return GoalEntity(
        id = id,
        title = title,
        category = category.title,
        startDate = goalStartDate,
        endDate = goalEndDate,
        description = description,
        coverUri = coverUri
    )
}

fun Milestone.toMilestoneEntity(goalId: Long) : MilestoneEntity{
    return MilestoneEntity(
        id = id,
        title = title,
        goalId = goalId,
        isCompleted = isCompleted
    )
}


fun GoalWithMilestoneEntity.toGoal(): Goal {
    return Goal(
        id = goalEntity.id,
        category = GoalCategory.valueOf(goalEntity.category.uppercase()),
        title = goalEntity.title,
        description = goalEntity.description,
        goalStartDate = goalEntity.startDate,
        goalEndDate = goalEntity.endDate,
        milestones = milestones.map { it.toMilestone() },
        coverUri = goalEntity.coverUri
    )
}
