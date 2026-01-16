package com.example.habitflow.data.local.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {

    @Query(
        """
            SELECT * FROM Tasks
        """
    )
    fun getTasks() : Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(taskEntity: TaskEntity)

    @Query(
        """
            UPDATE Tasks
            SET isCompleted = NOT isCompleted
            WHERE id == :taskId
        """
    )
    suspend fun changeTaskCompletedState(taskId: Int)

    @Query(
        """
            DELETE FROM TASKS
            WHERE id == :taskId
        """
    )
    suspend fun deleteTask(taskId: Int)


}