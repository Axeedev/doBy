package com.example.habitflow.data.local.goals

import androidx.room.Embedded
import androidx.room.Relation

data class GoalWithMilestoneEntity(
    @Embedded
    val goalEntity: GoalEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val milestones: List<MilestoneEntity>
)
