package com.example.habitflow.data.mappers

import com.example.habitflow.data.local.achievements.AchievementEntity
import com.example.habitflow.data.local.goals.GoalEntity
import com.example.habitflow.data.local.goals.GoalWithMilestoneEntity
import com.example.habitflow.data.local.goals.MilestoneEntity
import com.example.habitflow.data.local.tasks.CompletedTaskEntity
import com.example.habitflow.data.local.tasks.TaskEntity
import com.example.habitflow.data.sync.CompletedTaskDto
import com.example.habitflow.data.sync.TaskDto
import com.example.habitflow.domain.entities.achievements.Achievement
import com.example.habitflow.domain.entities.goals.Goal
import com.example.habitflow.domain.entities.Category
import com.example.habitflow.domain.entities.goals.Milestone
import com.example.habitflow.domain.entities.tasks.CompletedTask
import com.example.habitflow.domain.entities.tasks.Priority
import com.example.habitflow.domain.entities.tasks.Task

fun Task.toTaskEntity(
    taskId: Int
): TaskEntity{
    return TaskEntity(
        id = taskId ,
        title = title ,
        deadlineMillis = deadlineMillis ,
        note = note,
        isCompleted = isCompleted,
        category = category.name,
        priority = priority.name,
        isReturned = false
    )
}
fun TaskEntity.toTask(): Task{
    return Task(
        id = id ,
        title = title ,
        deadlineMillis = deadlineMillis,
        note = note,
        category = Category(category),
        priority = Priority.valueOf(this.priority.uppercase()),
        isCompleted = isCompleted,
        isReturned = isReturned
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
        category = category.name,
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
        category = Category(goalEntity.category),
        title = goalEntity.title,
        description = goalEntity.description,
        goalStartDate = goalEntity.startDate,
        goalEndDate = goalEntity.endDate,
        milestones = milestones.map { it.toMilestone() },
        coverUri = goalEntity.coverUri
    )
}

fun TaskEntity.toCompletedTaskEntity(dateOfCompletion: Long): CompletedTaskEntity{
    return CompletedTaskEntity(
        id = id,
        title = title,
        deadlineMillis = deadlineMillis,
        note = note,
        category = category,
        priority = priority,
        completedAt = dateOfCompletion
    )
}

fun CompletedTaskEntity.toTaskEntity(): TaskEntity{
    return TaskEntity(
        id = id ,
        title = title ,
        deadlineMillis = deadlineMillis ,
        note = note ,
        category = category ,
        priority = priority ,
        isCompleted = false,
        isReturned = true
    )
}

fun CompletedTaskEntity.toCompletedTask() : CompletedTask{
    return CompletedTask(
        id = id,
        title = title,
        deadlineMillis = deadlineMillis,
        note = note,
        category = Category(category),
        priority = Priority.valueOf(priority.uppercase()),
        isCompleted = isCompleted,
        completionDate = completedAt
    )
}

fun AchievementEntity.toAchievement() = Achievement(
    id = id ,
    titleId = titleId ,
    descriptionId = descriptionId ,
    targetGoal = targetGoal ,
    currentScore = currentProgress ,
    iconResId = iconId,
    achievementType = achievementType,
)
fun Achievement.toAchievementEntity() = AchievementEntity(
    id = id ,
    titleId = titleId ,
    descriptionId = descriptionId ,
    targetGoal = targetGoal ,
    currentProgress = currentScore ,
    iconId = iconResId,
    achievementType = achievementType
)

fun TaskEntity.toDto(remoteId: String? = null) = TaskDto(
    id = remoteId,
    title = title,
    deadlineMillis = deadlineMillis,
    note = note,
    category = category,
    priority = priority,
    completed = isCompleted,
    returned = isReturned,
    updatedAt = updatedAt,
    deleted = isDeleted
)

fun TaskDto.toEntity(existingLocalId: Int? = null) = TaskEntity(
    id = existingLocalId ?: 0,
    remoteId = id,
    title = title,
    deadlineMillis = deadlineMillis,
    note = note,
    category = category,
    priority = priority,
    isCompleted = completed,
    isReturned = returned,
    updatedAt = updatedAt,
    isDeleted = deleted,
    isSynced = true
)

fun CompletedTaskEntity.toDto(remoteId: String?): CompletedTaskDto{
    return CompletedTaskDto(
        id = remoteId,
        title = title,
        deadlineMillis = deadlineMillis,
        note = note,
        category = category,
        priority = priority,
        completed = isCompleted,
        updatedAt = updatedAt,
        completedAt = completedAt
    )
}


fun CompletedTaskDto.toEntity(existingLocalId: Int? = null) = CompletedTaskEntity(
    id = existingLocalId ?: 0,
    remoteId = id,
    title = title,
    deadlineMillis = deadlineMillis,
    note = note,
    category = category,
    priority = priority,
    isCompleted = completed,
    completedAt = completedAt,
    updatedAt = updatedAt,
    isSynced = true
)