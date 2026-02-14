package com.example.habitflow.data.local.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitflow.domain.entities.DailyStat
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
        ORDER BY completedAt DESC
    """)
    fun getCompletedTasks() : Flow<List<CompletedTaskEntity>>

    @Query("""
        SELECT * FROM completedTasks
        WHERE id == :taskId
    """)
    suspend fun getTaskById(taskId: Int): CompletedTaskEntity

    @Query("""
    SELECT 
        (completedAt / 86400000)  AS completedAt, 
        COUNT(*) AS count 
    FROM completedTasks 
    GROUP BY (completedAt / 86400000)
    ORDER BY completedAt ASC
""")
    fun getDailyStatsMap(): Flow<Map<
            @MapColumn(columnName = "completedAt") Long,
            @MapColumn(columnName = "count") Int
            >>


    @Query("""
    SELECT 
        (completedAt / 86400000) * 86400000 AS completedAt, 
        COUNT(*) AS count 
    FROM completedTasks  
    GROUP BY completedAt 
    ORDER BY completedAt ASC
""")
    fun getDailyStats(): Flow<List<DailyStat>>

    @Query(
        """
            SELECT COUNT(*) from completedTasks
        """
    )
    fun getCompletedTasksSize() : Flow<Int>

}