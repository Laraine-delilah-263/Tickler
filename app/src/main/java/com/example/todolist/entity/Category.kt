package com.example.todolist.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//分类实体
@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) val cataId: Long = 0,
    val label: String
)
