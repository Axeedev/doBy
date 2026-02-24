package com.example.habitflow.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DataSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val tasksRef by lazy {
        firestore
            .collection(USERS_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(TASKS_COLLECTION)
    }

    suspend fun createTask(taskDto: TaskDto): String {
        val doc = tasksRef.document()
        doc.set(taskDto.copy(id = doc.id)).await()
        return doc.id
    }

    suspend fun updateTask(task: TaskDto) {
        task.id ?: return
        tasksRef.document(task.id).set(task).await()
    }

    suspend fun getAll(): List<TaskDto> =
        tasksRef.get().await().toObjects(TaskDto::class.java)

    companion object {

        const val USERS_COLLECTION = "users"
        const val TASKS_COLLECTION = "tasks"

    }
}