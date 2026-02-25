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
            WHERE isDeleted = 0
            ORDER BY deadlineMillis ASC
        """
    )
    fun getTasks() : Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(taskEntity: TaskEntity) : Long

    @Query(
        """
            UPDATE Tasks
            SET isCompleted = NOT isCompleted
            WHERE id == :taskId
        """
    )
    suspend fun changeTaskCompletedState(taskId: Int)

    @Query("""
        SELECT * FROM tasks
        WHERE id == :taskId
            """)
    suspend fun getTaskById(taskId: Int) : TaskEntity

    @Query(
        """
            DELETE FROM TASKS
            WHERE id == :taskId
        """
    )
    suspend fun deleteTask(taskId: Int)

    @Query("""
        SELECT * FROM tasks
        WHERE deadlineMillis > :currentTime
    """)
    suspend fun getScheduledTasksWithFutureDeadline(
        currentTime: Long
    ) : List<TaskEntity>


    @Query("""
        SELECT COUNT(*) FROM tasks
        WHERE deadlineMillis BETWEEN :start AND :end
    """)
    suspend fun getCountOfScheduledTasksForPeriod(
        start: Long,
        end: Long
    ) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFromRemoteToLocal(taskEntity: TaskEntity) : Long

    @Query("""
        SELECT * FROM tasks
        WHERE remoteId = :remoteId
    """)
    suspend fun getByIdRemote(remoteId: String) : TaskEntity?

    @Query("""
        UPDATE tasks
        SET isSynced = 1, remoteId = :remoteId
        WHERE id = :localId
    """)
    suspend fun updateRemoteId(remoteId: String, localId: Int)

    @Query("""
        SELECT * FROM tasks
        WHERE isSynced = 0
    """)
    suspend fun getUnsyncedTasks() : List<TaskEntity>

    @Query("""
        UPDATE tasks
        SET isSynced = 1
        WHERE id == :localId
    """)
    suspend fun markTaskAsSynced(localId: Int)

}