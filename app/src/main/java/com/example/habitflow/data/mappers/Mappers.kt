package com.example.habitflow.data.mappers

import com.example.habitflow.data.local.goals.GoalEntity
import com.example.habitflow.data.local.goals.GoalWithMilestoneEntity
import com.example.habitflow.data.local.goals.MilestoneEntity
import com.example.habitflow.data.local.tasks.TaskEntity
import com.example.habitflow.domain.entities.Goal
import com.example.habitflow.domain.entities.Milestone
import com.example.habitflow.domain.entities.Task

fun Task.toTaskEntity(): TaskEntity{
    return TaskEntity(
        id = id ,
        title = title ,
        date = date ,
        note = note ,
        isCompleted = isCompleted,
        category = category,
        startTime = startTime,
        endTime = endTime,
        priority = priority
    )
}
fun TaskEntity.toTask(): Task{
    return Task(
        id = id ,
        title = title ,
        date = date ,
        note = note ,
        category = category,
        startTime = startTime,
        endTime = endTime,
        priority = priority,
        isCompleted = isCompleted
    )
}

fun MilestoneEntity.toMilestone() : Milestone{

    return Milestone(
        id,
        title
    )

}

fun Goal.toGoalEntity() : GoalEntity{
    return GoalEntity(
        id = id,
        title = title,
        category = category,
        startDate = goalStartDate,
        endDate = goalEndDate,
        description = description,
    )
}

fun Milestone.toMilestoneEntity(goalId: Int) : MilestoneEntity{
    return MilestoneEntity(
        id = id,
        title = title,
        goalId = goalId
    )
}


fun GoalWithMilestoneEntity.toGoal(): Goal {
    return Goal(
        id = goalEntity.id,
        category = goalEntity.category,
        title = goalEntity.title,
        description = goalEntity.description,
        goalStartDate = goalEntity.startDate,
        goalEndDate = goalEntity.endDate,
        milestones = milestones.map { it.toMilestone() }
    )
}
