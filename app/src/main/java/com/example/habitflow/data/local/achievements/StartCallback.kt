package com.example.habitflow.data.local.achievements

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habitflow.R
import com.example.habitflow.data.local.AppDatabase
import com.example.habitflow.domain.entities.achievements.AchievementType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class StartCallback @Inject constructor (
    private val databaseProvider: Provider<AppDatabase>,
) : RoomDatabase.Callback() {


    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            addStartAchievements()
        }
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
        super.onDestructiveMigration(db)
        CoroutineScope(Dispatchers.IO).launch {
            addStartAchievements()
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        CoroutineScope(Dispatchers.IO).launch {
            addStartAchievements()
        }
    }

    suspend fun addStartAchievements(){
        val startAchievements = listOf(
            AchievementEntity(
                id = 1,
                title = "First blood",
                description = "Complete 1 task",
                targetGoal = 1,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 2,
                title = "Decathlete",
                description = "Complete 10 tasks",
                targetGoal = 10,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 3,
                title = "Centurion",
                description = "Complete 100 tasks",
                targetGoal = 100,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 4,
                title = "Task Master",
                description = "Complete 500 tasks",
                targetGoal = 500,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 5,
                title = "Hot Streak",
                description = "Maintain a 3-day streak",
                targetGoal = 3,
                iconId = R.drawable.ic_fire,
                achievementType = AchievementType.STREAK
            ),
            AchievementEntity(
                id = 6,
                title = "On a Roll",
                description = "Maintain a 7-day streak",
                targetGoal = 7,
                iconId = R.drawable.ic_fire,
                achievementType = AchievementType.STREAK
            ),
            AchievementEntity(
                id = 7,
                title = "Unstoppable",
                description = "Maintain a 30-day streak",
                targetGoal = 30,
                iconId = R.drawable.ic_fire,
                achievementType = AchievementType.STREAK
            ),
            AchievementEntity(
                id = 8,
                title = "Early bird",
                description = "Complete a task after between 5:00 AM and 8:00 AM",
                achievementCode = AchievementCodes.EARLY_BIRD,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 9,
                title = "Night owl",
                description = "Complete a task between 0:00 AM and 4:00 AM",
                achievementCode = AchievementCodes.NIGHT_OWL,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 10,
                title = "Speedster",
                description = "Complete a task within 5 mins of creating it",
                achievementCode = AchievementCodes.SPEEDSTER,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 11,
                title = "Clutch",
                description = "Complete a task 1 minute before deadline",
                achievementCode = AchievementCodes.CLUTCH,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 12,
                title = "Monday Warrior",
                achievementCode = AchievementCodes.MONDAY_WARRIOR,
                description = "Complete 5 tasks on a Monday",
                targetGoal = 5,
                iconId = R.drawable.ic_checklist,
                achievementType = AchievementType.PRODUCTIVITY
            ),
            AchievementEntity(
                id = 13,
                title = "Overachiever",
                achievementCode = AchievementCodes.MARATHON,
                description = "Complete 10 tasks in a single day",
                targetGoal = 10,
                iconId = R.drawable.ic_checklist,
                achievementType = AchievementType.PRODUCTIVITY
            )
        )
        databaseProvider.get().achievementsDao().insertAchievements(startAchievements)
    }
}