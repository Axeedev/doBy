package com.example.habitflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habitflow.data.local.achievements.AchievementEntity
import com.example.habitflow.data.local.achievements.AchievementsDao
import com.example.habitflow.data.local.goals.GoalEntity
import com.example.habitflow.data.local.goals.GoalsDao
import com.example.habitflow.data.local.goals.MilestoneEntity
import com.example.habitflow.data.local.tasks.CompletedTaskEntity
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TaskEntity
import com.example.habitflow.data.local.tasks.TasksDao

@Database(
    entities = [
        TaskEntity::class,
        GoalEntity::class,
        MilestoneEntity::class,
        CompletedTaskEntity::class,
        AchievementEntity::class
    ],
    version = 86,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tasksDao(): TasksDao

    abstract fun goalsDao(): GoalsDao

    abstract fun completedTasksDao(): CompletedTasksDao

    abstract fun achievementsDao() : AchievementsDao
}