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
import com.example.habitflow.data.remote.Voice
import com.example.habitflow.data.remote.advice.ApiService
import com.example.habitflow.data.remote.voice.SaluteAuthService
import com.example.habitflow.data.remote.voice.SaluteSpeechApi
import com.example.habitflow.data.remote.Auth
import com.example.habitflow.data.remote.Chat
import com.example.habitflow.data.remote.summary.ChatApiService
import com.example.habitflow.data.remote.voice.getUnsafeOkHttpClient
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
import java.util.concurrent.TimeUnit
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

    @Singleton
    @Binds
    fun bindVoiceRepository(voiceRepositoryImpl: VoiceRepositoryImpl): VoiceRepository

    companion object {


        private const val X_API_KEY = "X-Api-Key"
        private const val BASE_URL = "https://api.api-ninjas.com/v1/"
        private const val AUTH_URL = "https://ngw.devices.sberbank.ru:9443/"
        private const val VOICE_URL = "https://smartspeech.sber.ru/"
        private const val SUMMARY_URL = "https://gigachat.devices.sberbank.ru/api/v1/"

        @Singleton
        @Provides
        @Chat
        fun provideChatRetrofit(
            converter: Converter.Factory,
            @Auth okHttpClient: OkHttpClient
        ) : Retrofit{
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(SUMMARY_URL)
                .addConverterFactory(converter)
                .build()
        }

        @Singleton
        @Provides
        fun provideChatApi(
            @Chat retrofit: Retrofit
        ) : ChatApiService = retrofit.create()



        @Singleton
        @Provides
        fun provideSaluteSpeechApi(
            @Voice retrofit: Retrofit
        ): SaluteSpeechApi = retrofit.create()


        @Auth
        @Provides
        @Singleton
        fun provideRetrofitAuth(
            converter: Converter.Factory,
            @Auth okHttpClient: OkHttpClient,
        ) = Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .client(okHttpClient)
            .addConverterFactory(converter)
            .build()

        @Singleton
        @Provides
        fun provideSaluteSpeechAuthService(@Auth retrofit: Retrofit) =
            retrofit.create<SaluteAuthService>()



        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Auth
        @Provides
        @Singleton
        fun provideVoiceOkHttpClient(
            interceptor: HttpLoggingInterceptor
        ) : OkHttpClient{
            return getUnsafeOkHttpClient()
                .addInterceptor(interceptor)
                .readTimeout(60_000, TimeUnit.SECONDS)
                .build()

        }

        @Provides
        @Singleton
        fun provideConverterFactory(json: Json): Converter.Factory {
            return json.asConverterFactory(contentType = "application/json".toMediaType())
        }

        @Singleton
        @Provides
        fun provideInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        @Voice
        @Provides
        @Singleton
        fun provideOkHttpClient(
            interceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            return OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    val request = chain.request()
                    val newRequest = request.newBuilder()
                        .addHeader(X_API_KEY, "0zoR+3VH8MaKmZxowuX+UA==yZxeFcZfj4Av9V7V")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        }

        @Voice
        @Provides
        @Singleton
        fun provideRetrofitVoice(
            @Auth okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(VOICE_URL)
                .addConverterFactory(converterFactory)
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(@Voice retrofit: Retrofit): ApiService {
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
        fun provideFirestore(): FirebaseFirestore {
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
        fun provideWorkManager(@ApplicationContext context: Context) =
            WorkManager.getInstance(context)
    }
}