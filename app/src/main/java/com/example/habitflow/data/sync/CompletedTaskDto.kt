package com.example.habitflow.data.sync

import com.google.firebase.firestore.PropertyName

data class CompletedTaskDto(
    val id: String? = null,
    val title: String = "",
    val deadlineMillis: Long? = null,
    val note: String = "",
    val category: String = "",
    val priority: String = "",
    @get:PropertyName("completed")
    val completed: Boolean = false,
    val completedAt: Long = System.currentTimeMillis(),
//    @get:PropertyName("returned")
//    val returned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
)
