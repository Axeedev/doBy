package com.example.habitflow.di

import android.content.Context
import androidx.room.Room
import com.example.habitflow.data.local.AppDatabase
import com.example.habitflow.data.local.achievements.AchievementsDao
import com.example.habitflow.data.local.achievements.StartCallback
import com.example.habitflow.data.local.goals.GoalsDao
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    companion object{
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
            startCallback: StartCallback
        ): AppDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "AppDatabase"
            ).addCallback(startCallback)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }

        @Provides
        @Singleton
        fun provideTasksDao(appDatabase: AppDatabase): TasksDao {
            return appDatabase.tasksDao()
        }


        @Singleton
        @Provides
        fun provideCompletedTasksDao(appDatabase: AppDatabase): CompletedTasksDao {
            return appDatabase.completedTasksDao()
        }

        @Provides
        @Singleton
        fun provideGoalsDao(appDatabase: AppDatabase): GoalsDao {
            return appDatabase.goalsDao()
        }

        @Singleton
        @Provides
        fun provideAchievementsDao(appDatabase: AppDatabase): AchievementsDao {
            return appDatabase.achievementsDao()
        }
    }
}