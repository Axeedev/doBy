package com.example.habitflow.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.credentials.CredentialManager
import androidx.work.WorkManager
import com.example.habitflow.data.local.NotificationsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SystemServiceModule {

    companion object{

        @Provides
        @Singleton
        fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager? {
            return context.getSystemService<AlarmManager>()
        }

        @Provides
        @Singleton
        fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager? {
            return context.getSystemService<NotificationManager>()
        }

        @Provides
        @Singleton
        fun provideNotificationsProvider(
            @ApplicationContext context: Context,
            notificationManager: NotificationManager?
        ): NotificationsProvider {
            return NotificationsProvider(
                context,
                notificationManager
            )
        }

        @Provides
        @Singleton
        fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
            return CredentialManager.create(context)
        }

        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context) =
            WorkManager.getInstance(context)
    }
}