package com.example.habitflow.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.credentials.CredentialManager
import androidx.room.Room
import androidx.work.WorkManager
import com.example.habitflow.data.local.AppDatabase
import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.data.local.achievements.AchievementsDao
import com.example.habitflow.data.local.achievements.StartCallback
import com.example.habitflow.data.local.goals.GoalsDao
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.remote.ApiService
import com.example.habitflow.data.repository.AchievementRepositoryImpl
import com.example.habitflow.data.repository.AnalyticsRepositoryImpl
import com.example.habitflow.data.repository.AuthRepositoryImpl
import com.example.habitflow.data.repository.GoalRepositoryImpl
import com.example.habitflow.data.repository.SettingsRepositoryImpl
import com.example.habitflow.data.repository.SyncRepositoryImpl
import com.example.habitflow.data.repository.TaskRepositoryImpl
import com.example.habitflow.domain.repository.AchievementRepository
import com.example.habitflow.domain.repository.AnalyticsRepository
import com.example.habitflow.domain.repository.AuthRepository
import com.example.habitflow.domain.repository.GoalRepository
import com.example.habitflow.domain.repository.SettingsRepository
import com.example.habitflow.domain.repository.SyncRepository
import com.example.habitflow.domain.repository.TaskRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

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

    companion object {


        private const val X_API_KEY = "X-Api-Key"
        private const val BASE_URL = "https://api.api-ninjas.com/v1/"

        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(json: Json): Converter.Factory {
            return json.asConverterFactory(contentType = "application/json".toMediaType())
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient
                .Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .addInterceptor { chain ->
                    val request = chain.request()
                    val newRequest = request.newBuilder()
                        .addHeader(X_API_KEY, "0zoR+3VH8MaKmZxowuX+UA==yZxeFcZfj4Av9V7V")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(converterFactory)
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService {
            return retrofit.create()
        }

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

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }

        @Singleton
        @Provides
        fun provideFirestore() : FirebaseFirestore{
            return FirebaseFirestore.getInstance()
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
        fun provideWorkManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)
    }
}