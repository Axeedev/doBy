package com.example.habitflow.data.local.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedTasksDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTaskToCompleted(completedTaskEntity: CompletedTaskEntity)

    @Query("""
        DELETE FROM completedTasks
        WHERE id == :taskId
    """)
    suspend fun deleteCompletedTaskById(taskId: Int)

    @Query("""
        SELECT DISTINCT * FROM completedTasks
        ORDER BY completedAt
    """)
    fun getCompletedTasks() : Flow<List<CompletedTaskEntity>>

    @Query("""
        SELECT * FROM COMPLETEDTASKS
        WHERE id == :taskId
    """)
    suspend fun getTaskById(taskId: Int): CompletedTaskEntity
}