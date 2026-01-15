package com.example.habitflow.data.local.tasks

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TasksDatabase : RoomDatabase(){

    abstract fun tasksDao(): TasksDao

}