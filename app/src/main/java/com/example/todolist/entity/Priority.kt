package com.example.todolist.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "priority")
data class Priority(
    @PrimaryKey(autoGenerate = true) val prioId: Long = 0,
    val urgent: String,
    val important: String,
    val common: String,
    val postpone: String,
    val finish: String
)
