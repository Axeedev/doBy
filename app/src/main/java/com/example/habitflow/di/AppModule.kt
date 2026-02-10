package com.example.habitflow.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.room.Room
import com.example.habitflow.data.local.AppDatabase
import com.example.habitflow.data.local.NotificationsProvider
import com.example.habitflow.data.local.goals.GoalsDao
import com.example.habitflow.data.local.tasks.CompletedTasksDao
import com.example.habitflow.data.local.tasks.TasksDao
import com.example.habitflow.data.remote.ApiService
import com.example.habitflow.data.repository.GoalRepositoryImpl
import com.example.habitflow.data.repository.SettingsRepositoryImpl
import com.example.habitflow.data.repository.TaskRepositoryImpl
import com.example.habitflow.domain.repository.GoalRepository
import com.example.habitflow.domain.repository.SettingsRepository
import com.example.habitflow.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
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
    fun bindGoalRepository(goalRepositoryImpl: GoalRepositoryImpl) : GoalRepository

    @Binds
    @Singleton
    fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl) : TaskRepository

    @Singleton
    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl) : SettingsRepository

    companion object{

        private const val X_API_KEY = "X-Api-Key"
        private const val BASE_URL = "https://api.api-ninjas.com/v1/"

        @Provides
        @Singleton
        fun provideJson(): Json{
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(json: Json): Converter.Factory{
            return json.asConverterFactory(contentType = "application/json".toMediaType())
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient{
            return OkHttpClient
                .Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .addInterceptor {chain ->
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
        fun provideRetrofit(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit{
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(converterFactory)
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService{
            return retrofit.create()
        }

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "TasksDatabase"
            ).fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }

        @Provides
        @Singleton
        fun provideTasksDao(appDatabase: AppDatabase) : TasksDao{
            return appDatabase.tasksDao()
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth{
            return FirebaseAuth.getInstance()
        }

        @Singleton
        @Provides
        fun provideCompletedTasksDao(appDatabase: AppDatabase): CompletedTasksDao{
            return appDatabase.completedTasksDao()
        }

        @Provides
        @Singleton
        fun provideGoalsDao(appDatabase: AppDatabase): GoalsDao{
            return appDatabase.goalsDao()
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
        ) : NotificationsProvider{
            return NotificationsProvider(
                context,
                notificationManager
            )
        }
    }
}