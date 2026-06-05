package com.example.todolist.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//紧急程度实体，优先级(tableName = "priority")
@Entity
data class Priority(
    @PrimaryKey(autoGenerate = true) var prioId: Long = 0,
    var levelName: String,    //优先级名称：紧急/重要/常规/暂缓/已完成

)