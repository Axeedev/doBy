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

    private suspend fun addStartAchievements(){
        val startAchievements = getStartAchievements()
        databaseProvider.get().achievementsDao().insertAchievements(startAchievements)
    }

    private fun getStartAchievements(): List<AchievementEntity>{
        return listOf(
            AchievementEntity(
                id = 1,
                titleId = R.string.achievement_first_blood,
                descriptionId = R.string.achievement_first_blood_description,
                targetGoal = 1,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 2,
                titleId = R.string.achievement_decathlete,
                descriptionId = R.string.achievement_decathlete_description,
                targetGoal = 10,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 3,
                titleId = R.string.achievement_centurion,
                descriptionId = R.string.achievement_centurion_description,
                targetGoal = 100,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 4,
                titleId = R.string.achievement_task_master,
                descriptionId = R.string.achievement_task_master_description,
                targetGoal = 500,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.TASKS_COMPLETED
            ),
            AchievementEntity(
                id = 5,
                titleId = R.string.achievement_hot_streak,
                descriptionId = R.string.achievement_hot_streak_description,
                targetGoal = 3,
                iconId = R.drawable.ic_fire,
                achievementType = AchievementType.STREAK
            ),
            AchievementEntity(
                id = 6,
                titleId = R.string.achievement_on_a_roll,
                descriptionId = R.string.achievement_on_a_roll_description,
                targetGoal = 7,
                iconId = R.drawable.ic_fire,
                achievementType = AchievementType.STREAK
            ),
            AchievementEntity(
                id = 7,
                titleId = R.string.achievement_unstoppable,
                descriptionId = R.string.achievement_unstoppable_description,
                targetGoal = 30,
                iconId = R.drawable.ic_fire,
                achievementType = AchievementType.STREAK
            ),
            AchievementEntity(
                id = 8,
                titleId = R.string.achievement_early_bird,
                descriptionId = R.string.achievement_early_bird_description,
                achievementCode = AchievementCodes.EARLY_BIRD,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 9,
                titleId = R.string.achievement_night_owl,
                descriptionId = R.string.achievement_night_owl_description,
                achievementCode = AchievementCodes.NIGHT_OWL,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 10,
                titleId = R.string.achievement_speedster,
                descriptionId = R.string.achievement_speedster_description,
                achievementCode = AchievementCodes.SPEEDSTER,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 11,
                titleId = R.string.achievement_clutch,
                descriptionId = R.string.achievement_clutch_description,
                achievementCode = AchievementCodes.CLUTCH,
                targetGoal = 1,
                iconId = R.drawable.ic_time,
                achievementType = AchievementType.TIMING
            ),
            AchievementEntity(
                id = 12,
                titleId = R.string.achievement_monday_warrior,
                achievementCode = AchievementCodes.MONDAY_WARRIOR,
                descriptionId = R.string.achievement_monday_warrior_description,
                targetGoal = 5,
                iconId = R.drawable.ic_checklist,
                achievementType = AchievementType.PRODUCTIVITY
            ),
            AchievementEntity(
                id = 13,
                titleId = R.string.achievement_overachiever,
                achievementCode = AchievementCodes.MARATHON,
                descriptionId = R.string.achievement_overachiever_description,
                targetGoal = 10,
                iconId = R.drawable.ic_checklist,
                achievementType = AchievementType.PRODUCTIVITY
            ),
            AchievementEntity(
                id = 14,
                titleId = R.string.achievement_first_steps,
                descriptionId = R.string.achievement_first_steps_description,
                targetGoal = 1,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.GOALS_COMPLETED
            ),
            AchievementEntity(
                id = 15,
                titleId = R.string.achievement_perfect_ten,
                descriptionId = R.string.achievement_perfect_ten_description,
                targetGoal = 10,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.GOALS_COMPLETED
            ),
            AchievementEntity(
                id = 16,
                titleId = R.string.achievement_goal_master,
                descriptionId = R.string.achievement_goal_master_description,
                targetGoal = 100,
                iconId = R.drawable.ic_task_app,
                achievementType = AchievementType.GOALS_COMPLETED
            )
        )
    }

}