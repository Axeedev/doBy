package com.example.habitflow.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CompletedTasksSyncManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val completedTasksRef by lazy {
        firestore
            .collection(USERS_COLLECTION)
            .document(firebaseAuth.uid!!)
            .collection(COMPLETED_TASKS_COLLECTION)
    }

    suspend fun createTask(taskDto: CompletedTaskDto): String {
        val doc = completedTasksRef.document()
        doc.set(taskDto.copy(id = doc.id)).await()
        return doc.id
    }

    suspend fun updateTask(task: CompletedTaskDto) {
        task.id ?: return
        completedTasksRef.document(task.id).set(task).await()
    }
    suspend fun delete(remoteId: String){
        completedTasksRef
            .document(remoteId)
            .delete()
            .await()
    }

    suspend fun getAll(): List<CompletedTaskDto> =
        completedTasksRef.get().await().toObjects(CompletedTaskDto::class.java)

    companion object {

        const val USERS_COLLECTION = "users"
        const val COMPLETED_TASKS_COLLECTION = "completed_tasks"

    }
}