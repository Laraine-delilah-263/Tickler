package com.example.todolist.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


//事务实体
@Entity(
    tableName = "todo_affair",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["cataId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Priority::class,
            parentColumns = ["prioId"],
            childColumns = ["priorityId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TodoAffair(
    @PrimaryKey(autoGenerate = true) val affId: Long = 0,
    val title: String,
    val detail: String,
    val startTime: Long,
    val endTime: Long,
    val isExpired: Int = 0,
    val isFinish: Int = 0,
    val categoryId: Long?,
    val priorityId: Long?
)
