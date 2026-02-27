package com.example.habitflow.di

import com.example.habitflow.data.repository.AchievementRepositoryImpl
import com.example.habitflow.data.repository.AnalyticsRepositoryImpl
import com.example.habitflow.data.repository.AuthRepositoryImpl
import com.example.habitflow.data.repository.GoalRepositoryImpl
import com.example.habitflow.data.repository.SettingsRepositoryImpl
import com.example.habitflow.data.repository.SyncRepositoryImpl
import com.example.habitflow.data.repository.TaskRepositoryImpl
import com.example.habitflow.data.repository.VoiceRepositoryImpl
import com.example.habitflow.domain.repository.AchievementRepository
import com.example.habitflow.domain.repository.AnalyticsRepository
import com.example.habitflow.domain.repository.AuthRepository
import com.example.habitflow.domain.repository.GoalRepository
import com.example.habitflow.domain.repository.SettingsRepository
import com.example.habitflow.domain.repository.SyncRepository
import com.example.habitflow.domain.repository.TaskRepository
import com.example.habitflow.domain.repository.VoiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindGoalRepository(goalRepositoryImpl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Singleton
    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Singleton
    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindAnalyticsRepository(analyticsRepositoryImpl: AnalyticsRepositoryImpl): AnalyticsRepository

    @Singleton
    @Binds
    fun bindAchievementRepository(achievementRepositoryImpl: AchievementRepositoryImpl): AchievementRepository

    @Singleton
    @Binds
    fun bindSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository

    @Singleton
    @Binds
    fun bindVoiceRepository(voiceRepositoryImpl: VoiceRepositoryImpl): VoiceRepository
}