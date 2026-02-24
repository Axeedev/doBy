package com.example.habitflow.data.sync

import com.google.firebase.firestore.PropertyName

data class TaskDto(
    val id: String? = null,
    val title: String = "",
    val deadlineMillis: Long? = null,
    val note: String = "",
    val category: String = "",
    val priority: String = "",
    @get:PropertyName("completed")
    val completed: Boolean = false,
    @get:PropertyName("returned")
    val returned: Boolean = false,
    val updatedAt: Long = 0,
    val deleted: Boolean = false
)
